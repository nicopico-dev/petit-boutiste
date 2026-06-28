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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

@Suppress("RedundantSuspendModifier", "RedundantSuppression")
suspend fun DataString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): List<ByteItem> = withContext(dispatcher) {
    val bytes = hexStringValue.windowed(2, 2)

    if (groupDefinitions.isEmpty()) {
        return@withContext bytes.mapIndexed { index, value ->
            SingleByte(index, value)
        }
    }

    // Resolve variable values once for all definitions
    val variables = try {
        DefinitionVariableRegistry(groupDefinitions).computeVariableValues(this@toByteItems)
    } catch (_: Exception) {
        emptyMap()
    }

    // Resolve start/end indexes for each definition, skipping those that cannot be resolved
    // or are completely outside the bounds of the payload; preserve insertion order (sorting deferred)
    // TODO: Index-based sorting deferred — definitions are kept in insertion order for now
    val validGroupDefinitions = groupDefinitions.mapNotNull { definition ->
        val start = Calculator.compute(definition.startFormula, variables) ?: return@mapNotNull null
        val end = Calculator.compute(definition.endFormula, variables) ?: return@mapNotNull null
        if (start > bytes.lastIndex) return@mapNotNull null
        Triple(definition, start, end)
    }

    val result = mutableListOf<ByteItem>()
    var currentIndex = 0

    // Process each valid group definition
    for ((definition, startIndex, definitionEndIndex) in validGroupDefinitions) {
        // Add single bytes before the current group
        while (currentIndex < startIndex) {
            result.add(SingleByte(currentIndex, bytes[currentIndex]))
            currentIndex++
        }

        // Skip this group if it overlaps with a previous group
        if (currentIndex > startIndex) {
            continue
        }

        // Ensure we do not go outside the bounds of the payload
        val endIndex = min(definitionEndIndex, bytes.lastIndex)

        // Add the group
        val groupBytes = (startIndex..endIndex).map { bytes[it] }
        result.add(
            ByteGroup(
                bytes = groupBytes,
                startIndex = startIndex,
                definition = definition,
                incomplete = endIndex < definitionEndIndex
            )
        )
        currentIndex = definitionEndIndex + 1
    }

    // Add remaining single bytes after the last group
    while (currentIndex < bytes.size) {
        result.add(SingleByte(currentIndex, bytes[currentIndex]))
        currentIndex++
    }

    return@withContext result
}
