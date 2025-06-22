package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.ByteGroupDefinition

fun List<ByteGroupDefinition>.replace(
    old: ByteGroupDefinition,
    new: ByteGroupDefinition,
): List<ByteGroupDefinition> {
    val index = indexOf(old).also {
        require(it >= 0) {
            "$old is not present in $this"
        }
    }
    return toMutableList()
        .apply {
            set(index, new)
        }
        .toList()
}

fun List<ByteGroupDefinition>.removeAt(index: Int): List<ByteGroupDefinition> {
    return this.toMutableList()
        .apply {
            removeAt(index)
        }
        .toList()
}
