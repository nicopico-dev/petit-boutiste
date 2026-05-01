/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.state.InputType

class HexString(
    rawHexString: String
) : DataString {

    override val inputType: InputType = InputType.HEX

    override val hexStringValue: String = rawHexString.normalizeHexString()

    init {
        require(hexStringValue.length % 2 == 0) { "HexString \"$hexStringValue\" must have an even length" }
    }

    val byteCount: Int = hexStringValue.length / 2

    override fun isNotEmpty() = hexStringValue.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HexString) return false

        return hexStringValue == other.hexStringValue
    }

    override fun hashCode(): Int {
        return hexStringValue.hashCode()
    }

    override fun toString(): String {
        return "HexString(hexString='$hexStringValue')"
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
