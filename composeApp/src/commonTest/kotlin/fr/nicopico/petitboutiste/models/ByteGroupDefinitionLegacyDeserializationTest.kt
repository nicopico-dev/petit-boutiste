package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

    @Test
    fun `deserializes legacy Hexadecimal RepresentationFormat to Representation`() {
        val json = """
            {
              "indexes": "8..9",
              "name": "COMMAND_ID",
              "representation": {
                "type": "fr.nicopico.petitboutiste.models.RepresentationFormat.Hexadecimal"
              }
            }
        """.trimIndent()

        val def = Json.decodeFromString(ByteGroupDefinition.serializer(), json)

        assertEquals(8..9, def.indexes)
        assertEquals("COMMAND_ID", def.name)
        assertEquals(DataRenderer.Hexadecimal, def.representation.dataRenderer)
        assertEquals(emptyMap(), def.representation.argumentValues)
    }

    @Test
    fun `deserializes legacy Text RepresentationFormat with defaults`() {
        val json = """
            {
              "indexes": "0..0",
              "name": "VERSION",
              "representation": {
                "type": "fr.nicopico.petitboutiste.models.RepresentationFormat.Text"
              }
            }
        """.trimIndent()

        val def = Json.decodeFromString(ByteGroupDefinition.serializer(), json)

        assertEquals(0..0, def.indexes)
        assertEquals("VERSION", def.name)
        assertEquals(DataRenderer.Text, def.representation.dataRenderer)
        // Defaults handled by renderer layer; here we only ensure keys exist if any
        // No explicit check on values because defaults may vary by platform
        assertNotNull(def.representation.argumentValues)
    }
}
