package fr.nicopico.petitboutiste.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HexStringTest {

    @Test
    fun `normalizes hex string`() {
        // Given a hex string with mixed case and non-hex characters
        val rawHexString = "1a2B3c4D-5e6F"

        // When creating a HexString
        val hexString = HexString(rawHexString)

        // Then the hex string is normalized
        assertEquals("1A2B3C4D5E6F", hexString.hexString)
    }

    @Test
    fun `converts binary to hex`() {
        // Given a binary string
        val binaryString = "10101010"

        // When creating a HexString with binary input format
        val hexString = HexString(binaryString, InputFormat.BINARY)

        // Then the binary is converted to hex
        assertEquals("AA", hexString.hexString)
    }

    @Test
    fun `converts hex to binary`() {
        // Given a hex string
        val hexString = "1A2B"

        // When converting to binary
        val binaryString = HexString.hexToBinary(hexString)

        // Then the hex is converted to binary
        assertEquals("0001101000101011", binaryString)
    }

    @Test
    fun `handles empty binary string`() {
        // Given an empty binary string
        val binaryString = ""

        // When creating a HexString with binary input format
        val hexString = HexString(binaryString, InputFormat.BINARY)

        // Then an empty hex string is created
        assertEquals("", hexString.hexString)
    }

    @Test
    fun `handles invalid binary string`() {
        // Given an invalid binary string (contains characters other than 0 and 1)
        val invalidBinaryString = "10102"

        // When parsing the string
        val result = HexString.parse(invalidBinaryString, InputFormat.BINARY)

        // Then the result is null
        assertNull(result)
    }

    @Test
    fun `handles binary string with odd length`() {
        // Given a binary string with odd length
        val binaryString = "101"

        // When creating a HexString with binary input format
        val hexString = HexString(binaryString, InputFormat.BINARY)

        // Then the binary is padded and converted to hex
        assertEquals("05", hexString.hexString)
    }

    @Test
    fun `round trip conversion works`() {
        // Given a hex string
        val originalHex = "1A2B3C"

        // When converting to binary and back to hex
        val binary = HexString.hexToBinary(originalHex)
        val hexString = HexString(binary, InputFormat.BINARY)

        // Then the final hex matches the original
        assertEquals(originalHex, hexString.hexString)
    }
}
