/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.render

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

/**
 * Returns `true` if [other] is entirely contained in this [ByteItem],
 * meaning *all* the bytes of `other` are also in the `ByteItem`
 */
operator fun ByteItem.contains(other: ByteItem): Boolean = other.startIndex >= startIndex
    && other.endIndex <= endIndex

/**
 * Renders this [ByteItem] with the given [representation].
 *
 * If the [ByteItem] is a [ByteGroup] and the [representation] matches its definition's representation,
 * returns the cached rendering from the group. Otherwise, renders the byte item directly with the
 * representation.
 *
 * Returns [RenderResult.None] if the representation is not ready.
 */
suspend fun ByteItem.renderWith(representation: Representation): RenderResult {
    return if (representation.isReady) {
        // Use cached rendering for ByteGroup if the representation matches its definition
        if (this is ByteGroup && representation == this.definition.representation) {
            this.getOrComputeRendering()
        } else {
            representation.render(this)
        }
    } else {
        RenderResult.None
    }
}
