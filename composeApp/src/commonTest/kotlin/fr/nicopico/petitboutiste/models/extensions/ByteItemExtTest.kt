package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.RepresentationFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteItemExtTest {

    @Test
    fun `getRepresentation returns binary representation with bytes grouped and split into 4-bit groups`() {
        // Given a ByteItem with multiple bytes
        val byteItem = ByteItem.Group(
            bytes = listOf("1A", "2B", "3C"),
            definition = fr.nicopico.petitboutiste.models.ByteGroupDefinition(0..2, "TestGroup")
        )

        // When getting the binary representation
        val binaryRepresentation = byteItem.getRepresentation(RepresentationFormat.Binary)

        // Then the result is formatted with bytes grouped and each byte split into two 4-bit groups
        val expected = "0001 1010 0010 1011 0011 1100"
        assertEquals(expected, binaryRepresentation)
    }

    @Test
    fun `getRepresentation returns binary representation for a single byte`() {
        // Given a ByteItem with a single byte
        val byteItem = ByteItem.Single(0, "FF")

        // When getting the binary representation
        val binaryRepresentation = byteItem.getRepresentation(RepresentationFormat.Binary)

        // Then the result is formatted with the byte split into two 4-bit groups
        val expected = "1111 1111"
        assertEquals(expected, binaryRepresentation)
    }

    @Test
    fun `getRepresentation handles zero bytes correctly`() {
        // Given a ByteItem with a byte value of zero
        val byteItem = ByteItem.Single(0, "00")

        // When getting the binary representation
        val binaryRepresentation = byteItem.getRepresentation(RepresentationFormat.Binary)

        // Then the result is formatted with leading zeros
        val expected = "0000 0000"
        assertEquals(expected, binaryRepresentation)
    }
}
