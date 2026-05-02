/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.state.InputType

class BinaryString(
    rawBinaryString: String
) : DataString {

    override val inputType: InputType = InputType.BINARY

    // The original binary string (normalized)
    val value: String = rawBinaryString.normalizeBinaryString()

    // Convert binary to hex for DataString interface
    override val hexStringValue: String = binaryToHex(value)

    val byteCount: Int = value.length / 8

    init {
        // Ensure the binary string length is a multiple of 8 or empty
        require(value.isEmpty() || value.length % 8 == 0) {
            "BinaryString must have a length that is a multiple of 8"
        }
    }

    override fun isNotEmpty() = value.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BinaryString) return false

        // 'other' is smart-cast to BinaryString
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "BinaryString(binaryString='$value')"
    }

    companion object {
        fun parse(input: String): BinaryString? {
            return try {
                BinaryString(input)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        // Convert from HexString to BinaryString
        fun fromHexString(hexString: HexString): BinaryString {
            val binaryString = hexString.hexStringValue.map {
                it.digitToInt(16).toString(2).padStart(4, '0')
            }.joinToString("")
            return BinaryString(binaryString)
        }

        // Helper function to convert binary string to hex string
        private fun binaryToHex(binary: String): String {
            val result = StringBuilder()
            for (i in binary.indices step 8) {
                if (i + 8 <= binary.length) {
                    val byte = binary.substring(i, i + 8)
                    val hex = byte.chunked(4).joinToString("") { nibble ->
                        nibble.toInt(2).toString(16)
                    }
                    result.append(hex)
                }
            }
            return result.toString().uppercase()
        }
    }
}

// Extension function to normalize binary string (keep only 0s and 1s)
private fun String.normalizeBinaryString(): String = this
    .filter { it == '0' || it == '1' }
