package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.HexString

fun HexString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList()
): List<ByteItem> {
    // First, create all single byte items
    val singleByteItems = hexString
        .windowed(size = 2, step = 2)
        .mapIndexed { index, value -> index to ByteItem.Single(value) }
        .toMap()

    // Track which indices are part of groups
    val groupedIndices = mutableSetOf<Int>()

    // Create groups according to definitions
    val groups = groupDefinitions.mapNotNull { definition ->
        // Skip invalid ranges (outside the bounds of the hex string)
        if (definition.indexes.first < 0 || definition.indexes.last >= singleByteItems.size) {
            return@mapNotNull null
        }

        // Get the byte items for this group
        val groupBytes = definition.indexes.mapNotNull { index ->
            singleByteItems[index]?.also { groupedIndices.add(index) }
        }

        // Skip empty groups
        if (groupBytes.isEmpty()) {
            return@mapNotNull null
        }

        ByteItem.Group(groupBytes, definition)
    }

    // Add all ungrouped single byte items
    val ungroupedSingles = singleByteItems
        .filterKeys { index -> index !in groupedIndices }
        .values

    // Combine groups and ungrouped singles, preserving the original order
    return (groups + ungroupedSingles).sortedBy { byteItem ->
        when (byteItem) {
            is ByteItem.Single -> singleByteItems.entries.first { it.value == byteItem }.key
            is ByteItem.Group -> byteItem.definition.indexes.first
        }
    }
}
