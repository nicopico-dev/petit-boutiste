/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.representation.Representation

private val BYTE_VALUE_REGEX = Regex("[a-fA-F0-9]{2}")

sealed class ByteItem {

    abstract val firstIndex: Int
    abstract val lastIndex: Int

    data class Single(
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

    data class Group(
        val bytes: List<String>,
        val definition: ByteGroupDefinition,

        /**
         * Set to `true` if [bytes] do not match the definition size.
         * This means the payload is likely incomplete or the definition is incorrect
         */
        val incomplete: Boolean = false,
    ) : ByteItem() {

        @Deprecated("Use for preview only")
        constructor(
            index: Int,
            data: String,
            name: String? = null,
        ) : this(
            bytes = data.windowed(2, 2),
            definition = ByteGroupDefinition(
                indexes = index..<(index + (data.length / 2)),
                name = name,
            ),
        )

        init {
            require(bytes.isNotEmpty()) {
                "bytes must not be empty"
            }
            require(bytes.all { it.length == 2 }) {
                "Each bytes must have a length of 2"
            }
        }

        val name: String? = definition.name

        override val firstIndex: Int = definition.indexes.first
        /**
         * If the group is incomplete, `lastIndex` will be the actual index of the groups last byte
         */
        override val lastIndex: Int = definition.indexes.first + (bytes.count() - 1)

        override fun toString(): String {
            return bytes.joinToString(separator = "")
        }

        companion object {
            fun createFullPayload(
                dataString: DataString,
                representation: Representation,
            ): Group {
                return Group(
                    bytes = dataString.hexString.windowed(2, 2),
                    definition = ByteGroupDefinition(
                        indexes = 0..<(dataString.hexString.length / 2),
                        representation = representation,
                    )
                )
            }
        }
    }
}
