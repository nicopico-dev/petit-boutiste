package fr.nicopico.petitboutiste.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
    fun `fails when hex string has odd length`() {
        // Given a hex string with odd length
        val rawHexString = "1A2B3"

        // When creating a HexString, then it fails
        assertFailsWith<IllegalArgumentException> {
            HexString(rawHexString)
        }
    }

    @Test
    fun `isNotEmpty returns true for non-empty hex string`() {
        // Given a non-empty hex string
        val hexString = HexString("1A2B")

        // When checking if it's not empty
        val result = hexString.isNotEmpty()

        // Then the result is true
        assertTrue(result)
    }

    @Test
    fun `isNotEmpty returns false for empty hex string`() {
        // Given an empty hex string
        val hexString = HexString("")

        // When checking if it's not empty
        val result = hexString.isNotEmpty()

        // Then the result is false
        assertFalse(result)
    }
}
