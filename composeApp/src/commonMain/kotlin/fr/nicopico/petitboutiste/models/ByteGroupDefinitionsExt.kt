package fr.nicopico.petitboutiste.models

fun List<ByteGroupDefinition>.updateGroupDefinitions(
    selectedIndex: Int?,
    definition: ByteGroupDefinition,
): List<ByteGroupDefinition> {
    val update = if (selectedIndex == null) {
        this + definition
    } else {
        this.toMutableList()
            .apply {
                set(selectedIndex, definition)
            }
            .toList()
    }
    return update.sortedBy { it.indexes.start }
}

fun List<ByteGroupDefinition>.removeAt(index: Int): List<ByteGroupDefinition> {
    return this.toMutableList()
        .apply {
            removeAt(index)
        }
        .toList()
}
