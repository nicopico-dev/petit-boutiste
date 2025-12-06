package fr.nicopico.petitboutiste.utils

/**
 * Update the start of a range without changing its length
 */
fun IntRange.moveStart(newStart: Int): IntRange {
    return newStart..(newStart + (endInclusive - start))
}
