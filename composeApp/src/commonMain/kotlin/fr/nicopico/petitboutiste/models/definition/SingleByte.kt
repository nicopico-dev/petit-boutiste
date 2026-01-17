/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

private val BYTE_VALUE_REGEX = Regex("[a-fA-F0-9]{2}")

data class SingleByte(
    val index: Int,
    val value: String,
) : ByteItem() {

    init {
        require(value.matches(BYTE_VALUE_REGEX)) {
            "value must be a single byte in hex format"
        }
    }

    override val firstIndex: Int = index
    override val lastIndex: Int = index

    override fun toString(): String {
        return value
    }
}
