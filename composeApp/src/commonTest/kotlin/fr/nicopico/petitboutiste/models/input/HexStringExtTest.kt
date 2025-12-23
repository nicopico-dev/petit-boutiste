package fr.nicopico.petitboutiste.models.input

import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.data.toByteItems
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import kotlin.test.Test
import kotlin.test.assertContentEquals
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
        val expected = listOf(
            ByteItem.Single(0, "1A"),
            ByteItem.Single(1, "2B"),
            ByteItem.Single(2, "3C"),
            ByteItem.Single(3, "4D"),
        )
        assertEquals(expected, byteItems)
    }

    @Test
    fun `toByteItems handles single byte hex string`() {
        // Given a hex string with a single byte
        val hexString = HexString("FF")

        // When converting to byte items
        val byteItems = hexString.toByteItems()

        // Then the result is a list with a single ByteItem.Single
        val expected = listOf(ByteItem.Single(0, "FF"))
        assertEquals(expected, byteItems)
    }

    @Test
    fun `toByteItems creates a group from a range of bytes`() {
        // Given a hex string and a group definition
        val hexString = HexString("1A2B3C4D")
        val groupDefinition = ByteGroupDefinition(1..2, "TestGroup")

        // When converting to byte items with the group definition
        val byteItems = hexString.toByteItems(listOf(groupDefinition))

        // Then the result contains a group and ungrouped singles
        val expected = listOf(
            ByteItem.Single(0, "1A"),
            ByteItem.Group(
                bytes = listOf("2B", "3C"),
                definition = groupDefinition,
            ),
            ByteItem.Single(3, "4D"),
        )
        assertContentEquals(expected, byteItems)
    }

    @Test
    fun `toByteItems creates multiple groups`() {
        // Given a hex string and multiple group definitions
        val hexString = HexString("1A2B3C4D5E6F")
        val group1Definition = ByteGroupDefinition(0..1, "Group1")
        val group2Definition = ByteGroupDefinition(3..4, "Group2")

        // When converting to byte items with the group definitions
        val byteItems = hexString.toByteItems(listOf(group1Definition, group2Definition))

        // Then the result contains both groups and ungrouped singles
        val expected = listOf(
            ByteItem.Group(
                listOf("1A", "2B"),
                group1Definition,
            ),
            ByteItem.Single(2, "3C"),
            ByteItem.Group(
                listOf("4D", "5E"),
                group2Definition,
            ),
            ByteItem.Single(5, "6F")
        )
        assertContentEquals(expected, byteItems)
    }

    @Test
    fun `toByteItems ignores overlapping groups`() {
        // Given a hex string and overlapping group definitions
        val hexString = HexString("1A2B3C4D")
        val group1Definition = ByteGroupDefinition(0..2, "Group1")
        // Overlaps with Group1
        val group2Definition = ByteGroupDefinition(1..3, "Group2")

        // When converting to byte items with the group definitions
        val byteItems = hexString.toByteItems(listOf(group1Definition, group2Definition))

        // Then the result contains only the first group
        val expected = listOf(
            ByteItem.Group(
                listOf("1A", "2B", "3C"),
                group1Definition,
            ),
            ByteItem.Single(3, "4D")
        )
        assertContentEquals(expected, byteItems)
    }

    @Test
    fun `toByteItems marks out-of-bounds definitions`() {
        // Given a hex string and an invalid group definition
        val hexString = HexString("1A2B3C4D")
        val groupDefinition1 = ByteGroupDefinition(1..2, "Valid (completely in bound)")
        val groupDefinition2 = ByteGroupDefinition(3..5, "Valid (end out of bounds)")
        val groupDefinition3 = ByteGroupDefinition(6..10, "Invalid: start outside of bounds")

        // When converting to byte items with the group definitions
        val byteItems = hexString.toByteItems(
            listOf(groupDefinition1, groupDefinition2, groupDefinition3)
        )

        // Then only the valid group is included
        val expected = listOf(
            ByteItem.Single(0, "1A"),
            ByteItem.Group(
                listOf("2B", "3C"),
                groupDefinition1
            ),
            ByteItem.Group(
                listOf("4D"),
                groupDefinition2,
                incomplete = true,
            ),
        )
        assertContentEquals(expected, byteItems)
    }
}
