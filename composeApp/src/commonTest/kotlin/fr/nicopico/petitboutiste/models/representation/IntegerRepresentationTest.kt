package fr.nicopico.petitboutiste.models.representation

import fr.nicopico.petitboutiste.models.ByteItem
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegerRepresentationTest {

    private lateinit var representation: Representation

    @BeforeTest
    fun setUp() {
        representation = Representation(DataRenderer.Integer)
    }

    @Test
    fun `should convert byte '00' to 0`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "00")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("0", output)
    }

    @Test
    fun `should convert byte '09' to 9`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "09")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("9", output)
    }

    @Test
    fun `should convert byte '0A' to 10`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "0A")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("10", output)
    }

    @Test
    fun `should convert byte '10' to 16`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "10")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("16", output)
    }

    @Test
    fun `should convert byte '1A' to 26`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "1A")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("26", output)
    }

    @Test
    fun `should convert byte '7F' to 127`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "7F")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("127", output)
    }

    @Test
    fun `should convert byte '80' to -128`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "80")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("-128", output)
    }

    @Test
    fun `should convert byte 'FF' to -1`() {
        // GIVEN
        val byteItem = ByteItem.Single(0, "FF")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("-1", output)
    }
}
