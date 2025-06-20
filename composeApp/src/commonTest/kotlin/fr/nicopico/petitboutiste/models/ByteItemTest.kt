package fr.nicopico.petitboutiste.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ByteItemTest {

    @Test
    fun `ByteItem Single creation with valid value`() {
        // Given a valid hex byte value
        val value = "1A"

        // When creating a ByteItem.Single
        val byteItem = ByteItem.Single(value)

        // Then the byte item is created with the correct value
        assertEquals(value, byteItem.value)
    }

    @Test
    fun `ByteItem Single creation fails with invalid value`() {
        // Given invalid hex byte values
        val tooShort = "A"
        val tooLong = "ABC"
        val nonHex = "XY"

        // When creating ByteItem.Single with invalid values, then it fails
        assertFailsWith<IllegalArgumentException> { ByteItem.Single(tooShort) }
        assertFailsWith<IllegalArgumentException> { ByteItem.Single(tooLong) }
        assertFailsWith<IllegalArgumentException> { ByteItem.Single(nonHex) }
    }

    @Test
    fun `ByteItem Single toString returns value`() {
        // Given a ByteItem.Single
        val byteItem = ByteItem.Single("1A")

        // When calling toString
        val result = byteItem.toString()

        // Then the result is the value
        assertEquals("1A", result)
    }

    @Test
    fun `ByteItem Group creation with valid bytes`() {
        // Given valid byte items
        val bytes = listOf(ByteItem.Single("1A"), ByteItem.Single("2B"))
        val name = "Test Group"

        // When creating a ByteItem.Group
        val byteGroup = ByteItem.Group(bytes, name)

        // Then the byte group is created with the correct values
        assertEquals(bytes, byteGroup.bytes)
        assertEquals(name, byteGroup.name)
    }

    @Test
    fun `ByteItem Group creation fails with empty bytes`() {
        // Given an empty list of bytes
        val emptyBytes = emptyList<ByteItem>()

        // When creating a ByteItem.Group with empty bytes, then it fails
        assertFailsWith<IllegalArgumentException> {
            ByteItem.Group(emptyBytes)
        }
    }

    @Test
    fun `ByteItem Group toString concatenates byte values`() {
        // Given a ByteItem.Group with multiple bytes
        val bytes = listOf(ByteItem.Single("1A"), ByteItem.Single("2B"), ByteItem.Single("3C"))
        val byteGroup = ByteItem.Group(bytes)

        // When calling toString
        val result = byteGroup.toString()

        // Then the result is the concatenation of the byte values
        assertEquals("1A2B3C", result)
    }

    @Test
    fun `ByteItem Group can contain nested groups`() {
        // Given a nested structure of byte items
        val innerGroup = ByteItem.Group(
            listOf(ByteItem.Single("1A"), ByteItem.Single("2B")),
            "Inner Group"
        )
        val outerGroup = ByteItem.Group(
            listOf(innerGroup, ByteItem.Single("3C")),
            "Outer Group"
        )

        // When accessing the bytes
        val innerBytes = innerGroup.bytes
        val outerBytes = outerGroup.bytes

        // Then the structure is maintained correctly
        assertEquals(2, innerBytes.size)
        assertEquals(2, outerBytes.size)
        assertEquals(innerGroup, outerBytes[0])
        assertEquals("1A2B3C", outerGroup.toString())
    }
}
