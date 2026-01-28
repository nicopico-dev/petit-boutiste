package fr.nicopico.petitboutiste.models.input

import fr.nicopico.petitboutiste.models.data.BinaryString
import fr.nicopico.petitboutiste.models.data.HexString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BinaryStringTest {

    @Test
    fun `normalizes binary string`() {
        // Given a binary string with non-binary characters
        val rawBinaryString = "0101 1010-0101 0101"

        // When creating a BinaryString
        val binaryString = BinaryString(rawBinaryString)

        // Then the binary string is normalized
        assertEquals("0101101001010101", binaryString.binaryString)
    }

    @Test
    fun `converts to hex string correctly`() {
        // Given a binary string
        val binaryString = BinaryString("0101101001010101")

        // Then it should be converted to the correct hex string
        assertEquals("5A55", binaryString.hexString)
    }

    @Test
    fun `requires length to be multiple of 8`() {
        // Given a binary string with length not multiple of 8
        val rawBinaryString = "0101010"

        // When creating a BinaryString, it should fail
        assertFailsWith<IllegalArgumentException> {
            BinaryString(rawBinaryString)
        }
    }

    @Test
    fun `creates from hex string`() {
        // Given a hex string
        val hexString = HexString("5A55")

        // When converting to binary string
        val binaryString = BinaryString.fromHexString(hexString)

        // Then it should have the correct binary representation
        assertEquals("0101101001010101", binaryString.binaryString)
    }

    @Test
    fun `handles empty string`() {
        // Given an empty string
        val rawBinaryString = ""

        // When creating a BinaryString, it should not fail
        val binaryString = BinaryString(rawBinaryString)

        // Then the binary string should be empty
        assertEquals("", binaryString.binaryString)
        assertEquals("", binaryString.hexString)
    }

    @Test
    fun `handles complex binary string`() {
        // Given a complex binary string
        val rawBinaryString = "01001000 01100101 01101100 01101100 01101111" // "Hello" in binary

        // When creating a BinaryString
        val binaryString = BinaryString(rawBinaryString)

        // Then it should be converted to the correct hex string
        assertEquals("48656C6C6F", binaryString.hexString)
    }
}
