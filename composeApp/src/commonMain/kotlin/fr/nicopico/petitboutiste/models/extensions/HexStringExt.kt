package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.HexString

fun HexString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList()
): List<ByteItem> {
    val bytes = hexString.windowed(2, 2)

    if (groupDefinitions.isEmpty()) {
        return bytes.mapIndexed { index, value -> ByteItem.Single(index, value) }
    }

    // Filter valid group definitions
    val validGroupDefinitions = groupDefinitions
        .filter { it.indexes.first >= 0 && it.indexes.last < bytes.size }
        .sortedBy { it.indexes.first }

    if (validGroupDefinitions.isEmpty()) {
        return bytes.mapIndexed { index, value -> ByteItem.Single(index, value) }
    }

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

        // Add the group
        val groupBytes = (definition.indexes.first..definition.indexes.last).map { bytes[it] }
        result.add(ByteItem.Group(groupBytes, definition))
        currentIndex = definition.indexes.last + 1
    }

    // Add remaining single bytes after the last group
    while (currentIndex < bytes.size) {
        result.add(ByteItem.Single(currentIndex, bytes[currentIndex]))
        currentIndex++
    }

    return result
}
