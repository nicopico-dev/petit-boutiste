package fr.nicopico.petitboutiste.models

fun HexString.toByteItems(): List<ByteItem> {
    return hexString
        .windowed(size = 2, step = 2)
        .map { ByteItem.Single(it) }
}
