/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.RenderResult

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

/**
 * Gets the cached rendering for this ByteItem.
 * For ByteGroup: returns the lazily-computed cached rendering.
 * For SingleByte: returns null as they use shared representations.
 */
suspend fun ByteItem.getRendering(): RenderResult? = when (this) {
    is ByteGroup -> getOrComputeRendering()
    is SingleByte -> null
}

fun ByteItem.toByteArray(): ByteArray {
    val hexString = rawHexString
    val len = hexString.length
    val data = ByteArray(len / 2)
    for (i in 0 until len step 2) {
        data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
    }
    return data
}
