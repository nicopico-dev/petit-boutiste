package fr.nicopico.petitboutiste.models

@Deprecated("")
sealed class OldByteItem

@Deprecated("")
data class SingleByte(val index: Int, val value: String) : OldByteItem()

@Deprecated("")
data class ByteGroup(
    val startIndex: Int,
    val endIndex: Int,
    val bytes: List<String>,
    val name: String
) : OldByteItem()
