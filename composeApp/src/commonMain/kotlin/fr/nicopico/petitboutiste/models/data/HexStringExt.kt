/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.SingleByte
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

suspend fun DataString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): List<ByteItem> = withContext(dispatcher) {
    val bytes = hexString.windowed(2, 2)

    if (groupDefinitions.isEmpty()) {
        return@withContext bytes.mapIndexed { index, value ->
            SingleByte(index, value)
        }
    }

    // Ignore definitions that are completely outside the bounds of the payload,
    // and sort by Start index
    val validGroupDefinitions = groupDefinitions
        .filter { it.indexes.first <= bytes.lastIndex }
        .sortedBy { it.indexes.first }

    val result = mutableListOf<ByteItem>()
    var currentIndex = 0

    // Process each valid group definition
    for (definition in validGroupDefinitions) {
        // Add single bytes before the current group
        while (currentIndex < definition.indexes.first) {
            result.add(SingleByte(currentIndex, bytes[currentIndex]))
            currentIndex++
        }

        // Skip this group if it overlaps with a previous group
        if (currentIndex > definition.indexes.first) {
            continue
        }

        // Ensure we do not go outside the bounds of the payload
        val endIndex = min(definition.indexes.last, bytes.lastIndex)

        // Add the group
        val groupBytes = (definition.indexes.first..endIndex).map { bytes[it] }
        result.add(
            ByteGroup(
                bytes = groupBytes,
                definition = definition,
                incomplete = endIndex < definition.indexes.last
            )
        )
        currentIndex = definition.indexes.last + 1
    }

    // Add remaining single bytes after the last group
    while (currentIndex < bytes.size) {
        result.add(SingleByte(currentIndex, bytes[currentIndex]))
        currentIndex++
    }

    return@withContext result
}
