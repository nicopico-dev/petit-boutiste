package fr.nicopico.petitboutiste.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

    @Test
    fun `toByteItems creates a group from a range of bytes`() {
        // Given a hex string and a group definition
        val hexString = HexString("1A2B3C4D")
        val groupDefinition = ByteGroupDefinition(1..2, "TestGroup")

        // When converting to byte items with the group definition
        val byteItems = hexString.toByteItems(listOf(groupDefinition))

        // Then the result contains a group and ungrouped singles
        assertEquals(3, byteItems.size)

        // First item should be the first single byte (index 0)
        assertTrue(byteItems[0] is ByteItem.Single)
        assertEquals("1A", (byteItems[0] as ByteItem.Single).value)

        // Second item should be the group (indices 1-2)
        assertTrue(byteItems[1] is ByteItem.Group)
        val group = byteItems[1] as ByteItem.Group
        assertEquals("TestGroup", group.name)
        assertEquals(2, group.bytes.size)
        assertEquals("2B", (group.bytes[0] as ByteItem.Single).value)
        assertEquals("3C", (group.bytes[1] as ByteItem.Single).value)

        // Third item should be the last single byte (index 3)
        assertTrue(byteItems[2] is ByteItem.Single)
        assertEquals("4D", (byteItems[2] as ByteItem.Single).value)
    }

    @Test
    fun `toByteItems creates multiple groups`() {
        // Given a hex string and multiple group definitions
        val hexString = HexString("1A2B3C4D5E6F")
        val groupDefinitions = listOf(
            ByteGroupDefinition(0..1, "Group1"),
            ByteGroupDefinition(3..4, "Group2")
        )

        // When converting to byte items with the group definitions
        val byteItems = hexString.toByteItems(groupDefinitions)

        // Then the result contains both groups and ungrouped singles
        assertEquals(4, byteItems.size)

        // First item should be the first group (indices 0-1)
        assertTrue(byteItems[0] is ByteItem.Group)
        val group1 = byteItems[0] as ByteItem.Group
        assertEquals("Group1", group1.name)
        assertEquals(2, group1.bytes.size)

        // Second item should be the single byte at index 2
        assertTrue(byteItems[1] is ByteItem.Single)
        assertEquals("3C", (byteItems[1] as ByteItem.Single).value)

        // Third item should be the second group (indices 3-4)
        assertTrue(byteItems[2] is ByteItem.Group)
        val group2 = byteItems[2] as ByteItem.Group
        assertEquals("Group2", group2.name)
        assertEquals(2, group2.bytes.size)

        // Fourth item should be the single byte at index 5
        assertTrue(byteItems[3] is ByteItem.Single)
        assertEquals("6F", (byteItems[3] as ByteItem.Single).value)
    }

    @Test
    fun `toByteItems handles overlapping groups`() {
        // Given a hex string and overlapping group definitions
        val hexString = HexString("1A2B3C4D")
        val groupDefinitions = listOf(
            ByteGroupDefinition(0..2, "Group1"),
            ByteGroupDefinition(1..3, "Group2") // Overlaps with Group1
        )

        // When converting to byte items with the group definitions
        val byteItems = hexString.toByteItems(groupDefinitions)

        // Then the result contains both groups with no duplicated bytes
        assertEquals(2, byteItems.size)

        // First item should be the first group (indices 0-2)
        assertTrue(byteItems[0] is ByteItem.Group)
        val group1 = byteItems[0] as ByteItem.Group
        assertEquals("Group1", group1.name)
        assertEquals(3, group1.bytes.size)

        // Second item should be the second group (indices 1-3)
        assertTrue(byteItems[1] is ByteItem.Group)
        val group2 = byteItems[1] as ByteItem.Group
        assertEquals("Group2", group2.name)
        assertEquals(3, group2.bytes.size)

        // Verify that no single bytes are left
        assertFalse(byteItems.any { it is ByteItem.Single })
    }

    @Test
    fun `toByteItems handles invalid ranges`() {
        // Given a hex string and an invalid group definition
        val hexString = HexString("1A2B3C4D")
        val groupDefinitions = listOf(
            ByteGroupDefinition(-1..1, "InvalidStart"), // Invalid start index
            ByteGroupDefinition(2..10, "InvalidEnd"),   // Invalid end index
            ByteGroupDefinition(1..2, "ValidGroup")     // Valid group
        )

        // When converting to byte items with the group definitions
        val byteItems = hexString.toByteItems(groupDefinitions)

        // Then only the valid group is included
        assertEquals(3, byteItems.size)

        // First item should be the first single byte (index 0)
        assertTrue(byteItems[0] is ByteItem.Single)
        assertEquals("1A", (byteItems[0] as ByteItem.Single).value)

        // Second item should be the valid group (indices 1-2)
        assertTrue(byteItems[1] is ByteItem.Group)
        val group = byteItems[1] as ByteItem.Group
        assertEquals("ValidGroup", group.name)
        assertEquals(2, group.bytes.size)

        // Third item should be the last single byte (index 3)
        assertTrue(byteItems[2] is ByteItem.Single)
        assertEquals("4D", (byteItems[2] as ByteItem.Single).value)
    }
}
