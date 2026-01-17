/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteGroupDefinitionLegacyDeserializationTest {

    @Test
    fun `deserializes legacy Integer RepresentationFormat to Representation with endianness`() {
        // Given legacy JSON using RepresentationFormat.Integer
        val json = """
            {
              "indexes": "10..11",
              "name": "NSEQ",
              "representation": {
                "endianness": "LittleEndian",
                "type": "fr.nicopico.petitboutiste.models.RepresentationFormat.Integer"
              }
            }
        """.trimIndent()

        // When decoding
        val def = Json.decodeFromString(ByteGroupDefinition.serializer(), json)

        // Then
        assertEquals(10..11, def.indexes)
        assertEquals("NSEQ", def.name)
        assertEquals(DataRenderer.Integer, def.representation.dataRenderer)
        assertEquals("LittleEndian", def.representation.argumentValues["endianness"])
    }
}
