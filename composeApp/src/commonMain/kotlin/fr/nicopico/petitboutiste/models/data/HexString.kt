/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

class HexString(
    rawHexString: String
) : DataString {

    override val hexString: String = rawHexString.normalizeHexString()

    init {
        require(hexString.length % 2 == 0) { "HexString \"$hexString\" must have an even length" }
    }

    val byteCount: Int = hexString.length / 2

    override fun isNotEmpty() = hexString.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HexString) return false

        other as HexString

        return hexString == other.hexString
    }

    override fun hashCode(): Int {
        return hexString.hashCode()
    }

    override fun toString(): String {
        return "HexString(hexString='$hexString')"
    }

    companion object {
        fun parse(input: String): HexString? {
            return try {
                HexString(input)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}

private fun String.normalizeHexString(): String = this
    .filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }
    .uppercase()
