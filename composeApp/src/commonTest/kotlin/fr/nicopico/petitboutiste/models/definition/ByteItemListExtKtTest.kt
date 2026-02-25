/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Representation
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteItemListExtKtTest {

    @Test
    fun `should convert a payload to json`() = runTest {
        // GIVEN
        val byteItems = listOf<ByteItem>(
            SingleByte(0, "AA"),
            ByteGroup(
                bytes = listOf("F0", "08"),
                definition = ByteGroupDefinition(
                    indexes = 1..2,
                    name = "Group 1",
                    representation = Representation(
                        dataRenderer = DataRenderer.Integer,
                    )
                )
            ),
            ByteGroup(
                bytes = listOf("AA"),
                definition = ByteGroupDefinition(
                    indexes = 3..3,
                    name = null,
                    representation = Representation(
                        dataRenderer = DataRenderer.Integer,
                    )
                )
            ),
            ByteGroup(
                bytes = listOf("41", "42", "43"),
                definition = ByteGroupDefinition(
                    indexes = 4..6,
                    name = "Group 3",
                    representation = Representation(
                        dataRenderer = DataRenderer.Text,
                    )
                )
            ),
        )

        // WHEN
        val json = byteItems.toJsonData()

        // THEN
        assertEquals(
            expected = """
                {
                    "Group 1": "-4088",
                    "Group 3": "ABC"
                }
            """.trimIndent(),
            actual = json,
        )
    }
}
