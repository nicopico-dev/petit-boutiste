/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.calculator.Calculator
import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.definition.expandFormulas
import fr.nicopico.petitboutiste.utils.logError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

@Suppress("RedundantSuspendModifier", "RedundantSuppression")
suspend fun DataString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    registry: DefinitionVariableRegistry? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): ByteItemsResult = withContext(dispatcher) {
    val bytes = hexStringValue.windowed(2, 2)
    val errors = mutableMapOf<String, String>()

    val finalRegistry = if (registry != null && registry.definitions == groupDefinitions) {
        registry
    } else {
        DefinitionVariableRegistry(groupDefinitions)
    }

    if (groupDefinitions.isEmpty()) {
        return@withContext ByteItemsResult(
            items = bytes.mapIndexed { index, value ->
                SingleByte(index, value)
            },
            errors = emptyMap(),
            registry = finalRegistry,
        )
    }

    // Resolve variable values at once for all definitions
    val variables = try {
        finalRegistry.computeVariableValues(this@toByteItems)
    } catch (e: Exception) {
        logError("Unable to compute variable values", e)
        // If the registry fails, it might be a global error (like circular dependency)
        // or a specific definition error. For now, we log it and continue with empty variables.
        // Formula resolution for specific definitions will likely fail too and be recorded below.
        emptyMap()
    }

    // Resolve start/end indexes for each definition, skipping those that cannot be resolved
    // or are completely outside the bounds of the payload
    val validGroupDefinitions = groupDefinitions.mapNotNull { definition ->
        try {
            val expandedDefinition = definition.expandFormulas()
            val start = Calculator.computeOrThrow(expandedDefinition.startFormula, variables)
            val end = Calculator.computeOrThrow(expandedDefinition.endFormula, variables)
            if (start > bytes.lastIndex) return@mapNotNull null
            Triple(definition, start, end)
        } catch (e: Exception) {
            errors[definition.id] = e.message ?: "Formula error"
            null
        }
    }.sortedBy { it.second } // Sort by resolved startIndex

    val result = mutableListOf<ByteItem>()
    var currentIndex = 0

    // Process each valid group definition
    for ((definition, startIndex, definitionEndIndex) in validGroupDefinitions) {
        // Add single bytes before the current group
        while (currentIndex < startIndex) {
            result.add(SingleByte(currentIndex, bytes[currentIndex]))
            currentIndex++
        }

        // Check for overlap
        if (currentIndex > startIndex) {
            errors[definition.id] = "Overlap detected at index $startIndex"
            continue
        }

        // Ensure we do not go outside the bounds of the payload
        val endIndex = min(definitionEndIndex, bytes.lastIndex)

        // Add the group
        val groupBytes = (startIndex..endIndex).map { bytes[it] }
        try {
            result.add(
                ByteGroup(
                    bytes = groupBytes,
                    startIndex = startIndex,
                    definition = definition,
                    incomplete = endIndex < definitionEndIndex
                )
            )
        } catch (e: IllegalArgumentException) {
            errors[definition.id] = e.message ?: "Invalid byte group"
            logError("Invalid byte group definition $definition", e)
        }
        currentIndex = definitionEndIndex + 1
    }

    // Add remaining single bytes after the last group
    while (currentIndex < bytes.size) {
        result.add(SingleByte(currentIndex, bytes[currentIndex]))
        currentIndex++
    }

    return@withContext ByteItemsResult(result, errors, finalRegistry)
}
