/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

infix fun <K, V> Map<K, V>.hasSameEntriesAs(other: Map<K, V>): Boolean {
    if (this.size != other.size) return false
    return this.all { (k, v) -> other[k] == v }
}

infix fun <K, V> Map<K, V>.hasDifferentEntriesFrom(other: Map<K, V>): Boolean {
    return !(this hasSameEntriesAs other)
}
