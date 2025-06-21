package fr.nicopico.petitboutiste.models

fun HexString.toByteItems(
    groupDefinitions: List<ByteGroupDefinition> = emptyList()
): List<ByteItem> {
    return hexString
        .windowed(size = 2, step = 2)
        .mapIndexed { index, value ->
            ByteItem.Single(value)
        }
}
