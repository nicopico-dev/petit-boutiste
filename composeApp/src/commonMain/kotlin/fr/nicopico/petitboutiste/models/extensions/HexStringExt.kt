package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.input.DataString
import kotlin.math.min

fun DataString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList()
): List<ByteItem> {
    val bytes = hexString.windowed(2, 2)

    if (groupDefinitions.isEmpty()) {
        return bytes.mapIndexed { index, value -> ByteItem.Single(index, value) }
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
            result.add(ByteItem.Single(currentIndex, bytes[currentIndex]))
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
            ByteItem.Group(
                bytes = groupBytes,
                definition = definition,
                incomplete = endIndex < definition.indexes.last
            )
        )
        currentIndex = definition.indexes.last + 1
    }

    // Add remaining single bytes after the last group
    while (currentIndex < bytes.size) {
        result.add(ByteItem.Single(currentIndex, bytes[currentIndex]))
        currentIndex++
    }

    return result
}
