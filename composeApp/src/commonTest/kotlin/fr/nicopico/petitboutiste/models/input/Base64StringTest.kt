package fr.nicopico.petitboutiste.models.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Base64StringTest {

    @Test
    fun `parse normalizes base64 string`() {
        // GIVEN
        val anyBase64String = "TG9yZW0gaXBzdW0"
        val normalizedBase64 = "TG9yZW0gaXBzdW0="

        // WHEN
        val result = Base64String.parse(anyBase64String)

        // THEN
        assertEquals(normalizedBase64, result?.base64String)
    }

    @Test
    fun `parse returns null if the input is not valid`() {
        // GIVEN
        val incompleteBase64String = "TG9yZW0gaXBzdW"

        // WHEN
        val result = Base64String.parse(incompleteBase64String)

        // THEN
        assertNull(result)
    }

    @Test
    fun `creates from hex string`() {
        // Given a hex string
        val hexString = HexString("4C6F72656D20697073756D")

        // When converting to base64 string
        val base64String = Base64String.fromHexString(hexString)

        // Then it should have the correct base64 representation
        assertEquals("TG9yZW0gaXBzdW0=", base64String.base64String)
    }

    @Test
    fun `converts to hex string correctly`() {
        // Given a hex string
        val hexString = HexString("4C6F72656D20697073756D")

        // When converting to base64 string
        val base64String = Base64String.fromHexString(hexString)

        // Then it should have the correct base64 representation
        assertEquals(hexString.hexString, base64String.hexString)
    }

    @Test
    fun `handles empty string`() {
        // Given an empty byte array
        val emptyData = ByteArray(0)

        // When creating a Base64String, it should not fail
        val base64String = Base64String(emptyData)

        // Then the binary string should be empty
        assertEquals("", base64String.base64String)
        assertEquals("", base64String.hexString)
    }
}
