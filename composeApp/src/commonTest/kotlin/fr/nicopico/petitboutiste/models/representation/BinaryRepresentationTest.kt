package fr.nicopico.petitboutiste.models.representation

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryRepresentationTest {

    private lateinit var representation: Representation

    @BeforeTest
    fun setUp() {
        representation = Representation(DataRenderer.Binary)
    }

    @Test
    fun `Binary representation returns binary with grouped bytes, split into 4-bit groups`() {
        // GIVEN
        // a ByteItem with multiple bytes
        val byteItem = ByteItem.Group(
            bytes = listOf("1A", "2B", "3C"),
            definition = ByteGroupDefinition(0..2, "TestGroup")
        )

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        // the result is formatted with bytes grouped, and each byte split into two 4-bit groups
        val expected = "0001 1010 0010 1011 0011 1100"
        assertEquals(expected, output)
    }

    @Test
    fun `getRepresentation returns binary representation for a single byte`() {
        // GIVEN
        // a ByteItem with a single byte
        val byteItem = ByteItem.Single(0, "FF")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        // the result is formatted with the byte split into two 4-bit groups
        val expected = "1111 1111"
        assertEquals(expected, output)
    }

    @Test
    fun `getRepresentation handles zero bytes correctly`() {
        // GIVEN
        // a ByteItem with a byte value of zero
        val byteItem = ByteItem.Single(0, "00")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        // the result is formatted with leading zeros
        val expected = "0000 0000"
        assertEquals(expected, output)
    }
}
