package fr.nicopico.petitboutiste.models.representation

import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.representation.arguments.SignednessArgument
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UnsignedIntegerRepresentationTest {

    private lateinit var representation: Representation

    @BeforeTest
    fun setUp() {
        representation = Representation(
            DataRenderer.Integer,
            argumentValues = mapOf(
                SignednessArgument.key to Signedness.Unsigned.name
            )
        )
    }

    @Test
    fun `should convert byte '00' to 0`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "00")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("0", output)
    }

    @Test
    fun `should convert byte '09' to 9`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "09")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("9", output)
    }

    @Test
    fun `should convert byte '0A' to 10`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "0A")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("10", output)
    }

    @Test
    fun `should convert byte '10' to 16`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "10")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("16", output)
    }

    @Test
    fun `should convert byte '1A' to 26`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "1A")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("26", output)
    }

    @Test
    fun `should convert byte '7F' to 127`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "7F")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("127", output)
    }

    @Test
    fun `should convert byte '80' to -128`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "80")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("128", output)
    }

    @Test
    fun `should convert byte 'FF' to -1`() = runTest {
        // GIVEN
        val byteItem = SingleByte(0, "FF")

        // WHEN
        val output = representation.renderAsString(byteItem)

        // THEN
        assertEquals("255", output)
    }
}
