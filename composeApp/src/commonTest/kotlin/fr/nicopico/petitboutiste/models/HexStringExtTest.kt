package fr.nicopico.petitboutiste.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HexStringExtTest {

    @Test
    fun `toByteItems converts empty hex string to empty list`() {
        // Given an empty hex string
        val hexString = HexString("")

        // When converting to byte items
        val byteItems = hexString.toByteItems()

        // Then the result is an empty list
        assertTrue(byteItems.isEmpty())
    }

    @Test
    fun `toByteItems converts hex string to list of single byte items`() {
        // Given a hex string
        val hexString = HexString("1A2B3C4D")

        // When converting to byte items
        val byteItems = hexString.toByteItems()

        // Then the result is a list of ByteItem.Single objects
        assertEquals(4, byteItems.size)
        assertTrue(byteItems[0] is ByteItem.Single)
        assertEquals("1A", (byteItems[0] as ByteItem.Single).value)
        assertTrue(byteItems[1] is ByteItem.Single)
        assertEquals("2B", (byteItems[1] as ByteItem.Single).value)
        assertTrue(byteItems[2] is ByteItem.Single)
        assertEquals("3C", (byteItems[2] as ByteItem.Single).value)
        assertTrue(byteItems[3] is ByteItem.Single)
        assertEquals("4D", (byteItems[3] as ByteItem.Single).value)
    }

    @Test
    fun `toByteItems handles single byte hex string`() {
        // Given a hex string with a single byte
        val hexString = HexString("FF")

        // When converting to byte items
        val byteItems = hexString.toByteItems()

        // Then the result is a list with a single ByteItem.Single
        assertEquals(1, byteItems.size)
        assertTrue(byteItems[0] is ByteItem.Single)
        assertEquals("FF", (byteItems[0] as ByteItem.Single).value)
    }
}
