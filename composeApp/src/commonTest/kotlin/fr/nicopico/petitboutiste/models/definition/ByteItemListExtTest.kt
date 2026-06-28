package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ByteItemListExtTest {

    @Test
    fun `toByteGroup returns null for empty list`() {
        val list = emptyList<ByteItem>()
        assertNull(list.toByteGroup())
    }

    @Test
    fun `toByteGroup returns null if contains non-SingleByte item`() {
        // Need to create a ByteGroup to put into the list
        val singleByte = SingleByte(0, "AA")
        createDefinitionId()
        val byteGroup = ByteGroup(
            bytes = listOf("BB"),
            startIndex = 1,
            definition = ByteGroupDefinition.createFromRange(
                indexes = 1..1,
                representation = DEFAULT_REPRESENTATION,
            )
        )
        val list = listOf(singleByte, byteGroup)
        assertNull(list.toByteGroup())
    }

    @Test
    fun `toByteGroup returns null for non-consecutive indices`() {
        val list = listOf(
            SingleByte(0, "AA"),
            SingleByte(2, "BB") // Indices 0 and 2 are not consecutive (should be 0 and 1)
        )
        assertNull(list.toByteGroup())
    }

    @Test
    fun `toByteGroup returns ByteGroup for consecutive SingleByte items`() {
        val list = listOf(
            SingleByte(0, "AA"),
            SingleByte(1, "BB"),
            SingleByte(2, "CC")
        )
        val byteGroup = list.toByteGroup()

        // Assertions
        assert(byteGroup != null)
        assertEquals(listOf("AA", "BB", "CC"), byteGroup!!.bytes)
        assertEquals(0..2, byteGroup.definition.indexes)
        assertEquals(DEFAULT_REPRESENTATION, byteGroup.definition.representation)
    }
}
