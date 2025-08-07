package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.BinaryString
import fr.nicopico.petitboutiste.models.HexString
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryStringExtTest {

    @Test
    fun `formats binary string for display`() {
        // Given a binary string
        val binaryString = BinaryString("0101101001010101")

        // When formatting for display
        val formatted = binaryString.formatForDisplay()

        // Then it should be properly formatted with spaces
        assertEquals("0101 1010 0101 0101", formatted)
    }

    @Test
    fun `formats empty binary string for display`() {
        // Given an empty binary string
        val binaryString = BinaryString("")

        // When formatting for display
        val formatted = binaryString.formatForDisplay()

        // Then it should return an empty string
        assertEquals("", formatted)
    }

    @Test
    fun `formats complex binary string for display`() {
        // Given a complex binary string (Hello in binary)
        val binaryString = BinaryString("0100100001100101011011000110110001101111")

        // When formatting for display
        val formatted = binaryString.formatForDisplay()

        // Then it should be properly formatted with spaces
        assertEquals("0100 1000 0110 0101 0110 1100 0110 1100 0110 1111", formatted)
    }

    @Test
    fun `converts hex string to binary string`() {
        // Given a hex string
        val hexString = HexString("48656C6C6F") // "Hello" in hex

        // When converting to binary string
        val binaryString = hexString.toBinaryString()

        // Then it should have the correct binary representation
        assertEquals("0100100001100101011011000110110001101111", binaryString.binaryString)
    }

    @Test
    fun `converts empty hex string to binary string`() {
        // Given an empty hex string
        val hexString = HexString("")

        // When converting to binary string
        val binaryString = hexString.toBinaryString()

        // Then it should return an empty binary string
        assertEquals("", binaryString.binaryString)
    }
}
