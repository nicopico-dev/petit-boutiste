/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

val ByteItem.name: String?
    get() = when (this) {
        is ByteGroup -> name
        is SingleByte -> null
    }

val ByteItem.size: Int
    get() = when (this) {
        is ByteGroup -> bytes.size
        is SingleByte -> 1
    }

val ByteItem.rawHexString: String
    get() = when (this) {
        is ByteGroup -> bytes.joinToString(separator = "")
        is SingleByte -> value
    }

fun ByteItem.toByteArray(): ByteArray {
    val hexString = rawHexString
    val len = hexString.length
    val data = ByteArray(len / 2)

    for (i in 0 until len step 2) {
        val highNibble = hexString[i].digitToInt(16)
        val lowNibble = hexString[i + 1].digitToInt(16)
        data[i / 2] = ((highNibble shl 4) + lowNibble).toByte()
    }

    return data
}
