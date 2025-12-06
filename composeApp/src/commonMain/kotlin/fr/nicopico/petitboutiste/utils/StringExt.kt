package fr.nicopico.petitboutiste.utils

private val INDEX_SUFFIX_PATTERN = Regex("(.*)([_\\-\\s])?(\\d+)$")

fun String.incrementIndexSuffix(): String {
    val match = INDEX_SUFFIX_PATTERN.find(this)
        ?: return "$this 2"

    val base = match.groupValues[1]
    val separator = match.groupValues[2]
    val counter = match.groupValues[3].toInt() + 1

    return "$base$separator$counter"
}
