package fr.nicopico.petitboutiste.utils

val pattern = Regex("(.*)(_-\\s)?(\\d+)$")

fun String.incrementIndexSuffix(): String {
    val match = pattern.find(this)
        ?: return "$this 2"

    val base = match.groupValues[1]
    val separator = match.groupValues[2]
    val counter = match.groupValues[3].toInt() + 1

    return "$base$separator$counter"
}
