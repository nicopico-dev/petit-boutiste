package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.EndiannessArgument
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests for [toByteItems] focused on formula-based index resolution.
 */
class DataStringExtTest {

    // region Static formulas (parity with old index-based behavior)

    @Test
    fun `toByteItems with static formulas produces same result as index-based splitting`() = runTest {
        // Given a payload and two non-overlapping group definitions with static formulas
        val data = HexString("1A2B3C4D5E6F")
        val def1 = ByteGroupDefinition.createFromRange(0..1, "Group1")
        val def2 = ByteGroupDefinition.createFromRange(3..4, "Group2")

        // When
        val byteItems = data.toByteItems(listOf(def1, def2))

        // Then the splitting matches the expected static behavior
        // [Group(0..1), Single(2), Group(3..4), Single(5)] = 4 items
        assertEquals(4, byteItems.size)
        assertIs<ByteGroup>(byteItems[0])
        assertEquals(listOf("1A", "2B"), (byteItems[0] as ByteGroup).bytes)
        assertIs<SingleByte>(byteItems[1])
        assertIs<ByteGroup>(byteItems[2])
        assertEquals(listOf("4D", "5E"), (byteItems[2] as ByteGroup).bytes)
        assertIs<SingleByte>(byteItems[3])
    }

    @Test
    fun `toByteItems with static out-of-bounds definition is skipped`() = runTest {
        // Given a short payload and a definition whose start is beyond the payload
        val data = HexString("1A2B3C")
        val outOfBoundsDef = ByteGroupDefinition.createFromRange(5..7, "TooFar")

        // When
        val byteItems = data.toByteItems(listOf(outOfBoundsDef))

        // Then all bytes are single bytes (no groups)
        assertTrue(byteItems.all { it is SingleByte })
        assertEquals(3, byteItems.size)
    }

    @Test
    fun `toByteItems with static definition partially out of bounds marks group incomplete`() = runTest {
        // Given a 4-byte payload and a definition that extends beyond it
        val data = HexString("1A2B3C4D")
        val def = ByteGroupDefinition.createFromRange(2..5, "PartialGroup")

        // When
        val byteItems = data.toByteItems(listOf(def))

        // Then the group exists but is marked incomplete, containing only the in-bounds bytes
        val group = byteItems.filterIsInstance<ByteGroup>().firstOrNull()
        assertEquals(listOf("3C", "4D"), group?.bytes)
        assertEquals(true, group?.incomplete)
    }

    // endregion

    // region Variable formulas

    @Test
    fun `toByteItems resolves variable formulas using LAST`() = runTest {
        // Given a 4-byte payload and a definition using [[LAST]] as end index
        val data = HexString("1A2B3C4D")
        val def = ByteGroupDefinition(
            name = "AllBytes",
            startFormula = "0",
            endFormula = "[[LAST]]",
        )

        // When
        val byteItems = data.toByteItems(listOf(def))

        // Then the group covers all bytes
        assertEquals(1, byteItems.size)
        val group = byteItems.first() as ByteGroup
        assertEquals(listOf("1A", "2B", "3C", "4D"), group.bytes)
        assertEquals(0, group.startIndex)
        assertEquals(3, group.endIndex)
    }

    @Test
    fun `toByteItems resolves cross-definition variable formulas`() = runTest {
        // Given a payload where the second group's start depends on the first group's end
        // Layout: [LEN=03][data: 3 bytes][rest]
        // LEN is at byte 0, value=3
        // Data starts at byte 1, ends at byte [[LEN.VALUE]]
        val data = HexString("031A2B3CFF")
        val lenDef = ByteGroupDefinition(
            name = "LEN",
            startFormula = "0",
            endFormula = "0",
            representation = Representation(
                dataRenderer = DataRenderer.Integer,
                argumentValues = mapOf(EndiannessArgument.key to Endianness.BigEndian.name)
            )
        )
        val dataDef = ByteGroupDefinition(
            name = "DATA",
            startFormula = "1",
            endFormula = "[[LEN.value]]",
        )
        val restDef = ByteGroupDefinition(
            name = "REST",
            startFormula = "[[LEN.value]] + 1",
            endFormula = "[[LAST]]",
        )

        // When
        val byteItems = data.toByteItems(listOf(lenDef, dataDef, restDef))

        // Then
        val groups = byteItems.filterIsInstance<ByteGroup>()
        assertEquals(3, groups.size)

        val lenGroup = groups.first { it.definition.name == "LEN" }
        assertEquals(listOf("03"), lenGroup.bytes)
        assertEquals(0, lenGroup.startIndex)

        val dataGroup = groups.first { it.definition.name == "DATA" }
        assertEquals(listOf("1A", "2B", "3C"), dataGroup.bytes)
        assertEquals(1, dataGroup.startIndex)

        val restGroup = groups.first { it.definition.name == "REST" }
        assertEquals(listOf("FF"), restGroup.bytes)
        assertEquals(4, restGroup.startIndex)
    }

    // endregion

    // region Error handling

    @Test
    fun `toByteItems skips definition with unresolvable formula without crashing`() = runTest {
        // Given a payload and a definition referencing an unknown variable
        val data = HexString("1A2B3C4D")
        val badDef = ByteGroupDefinition(
            name = "BadDef",
            startFormula = "[[UNKNOWN.start]]",
            endFormula = "[[UNKNOWN.end]]",
        )
        val goodDef = ByteGroupDefinition.createFromRange(0..1, "GoodDef")

        // When - should not throw
        val byteItems = data.toByteItems(listOf(badDef, goodDef))

        // Then the bad definition is silently skipped; the good one is resolved
        val groups = byteItems.filterIsInstance<ByteGroup>()
        assertEquals(1, groups.size)
        assertEquals("GoodDef", groups.first().definition.name)
    }

    @Test
    fun `toByteItems handles circular dependency gracefully without crashing`() = runTest {
        // Given definitions with a circular dependency
        val data = HexString("1A2B3C4D")
        val defA = ByteGroupDefinition(
            name = "A",
            startFormula = "0",
            endFormula = "[[B.start]] - 1",
        )
        val defB = ByteGroupDefinition(
            name = "B",
            startFormula = "[[A.end]] + 1",
            endFormula = "3",
        )

        // When - should not throw; the whole payload falls back to single bytes
        val byteItems = data.toByteItems(listOf(defA, defB))

        // Then all bytes are single (registry throws → variables empty → formulas with [[ fail to resolve)
        assertTrue(byteItems.all { it is SingleByte })
    }

    // endregion
}
