/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.state.InputType
import kotlin.io.encoding.Base64

class Base64String(
    private val byteArray: ByteArray = ByteArray(0),
) : DataString {

    override val inputType: InputType = InputType.BASE64

    val base64String: String = base64.encode(byteArray)

    override val hexString: String
        get() = byteArray.toHexString(HexFormat.UpperCase)

    override fun isNotEmpty() = base64String.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Base64String) return false
        return base64String == other.base64String
    }

    override fun hashCode(): Int {
        return base64String.hashCode()
    }

    override fun toString(): String {
        return "Base64String(base64String='$base64String')"
    }

    companion object {
        private val base64: Base64 = Base64.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)

        fun parse(input: String): Base64String? {
            return try {
                val byteArray = base64.decode(input)
                Base64String(byteArray)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        fun fromHexString(hexString: HexString): Base64String {
            val byteArray = hexString.hexString.hexToByteArray()
            return Base64String(byteArray)
        }
    }
}
