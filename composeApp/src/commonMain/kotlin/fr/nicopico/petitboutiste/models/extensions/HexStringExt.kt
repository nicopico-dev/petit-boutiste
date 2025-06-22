package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.HexString

fun HexString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList()
): List<ByteItem> {
    val bytes = hexString.windowed(2, 2)
    return if (groupDefinitions.isEmpty()) {
        bytes.mapIndexed { index, value -> ByteItem.Single(index, value) }
    } else TODO("Build ByteItem according to the group definitions")
}
