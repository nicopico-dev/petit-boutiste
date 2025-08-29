package fr.nicopico.petitboutiste.utils

infix fun <K, V> Map<K, V>.hasSameEntriesAs(other: Map<K, V>): Boolean {
    if (this.size != other.size) return false
    return this.all { (k, v) -> other[k] == v }
}

infix fun <K, V> Map<K, V>.hasDifferentEntriesFrom(other: Map<K, V>): Boolean {
    return !(this hasSameEntriesAs other)
}
