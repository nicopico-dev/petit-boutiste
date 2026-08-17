package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
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
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Tests for [toByteItems] focused on formula-based index resolution.
 */
class DataStringExtTest {

    // region Static formulas (parity with old index-based behavior)

    @Test
    fun `toByteItems with static formulas produces same result as index-based splitting`() = runTest {
        // Given a payload and two non-overlapping groups defined out of order
        val data = HexString("1A2B3C4D5E6F")
        val earlyDef = ByteGroupDefinition.createFromRange(0..1, "Early")
        val lateDef = ByteGroupDefinition.createFromRange(3..4, "Late")

        // When - definitions are provided in reverse insertion order
        val byteItems = data.toByteItems(listOf(lateDef, earlyDef)).items

        // Then - groups are interleaved based on resolved start indexes
        // Expected items: [Group(0..1), Single(2), Group(3..4), Single(5)]
        assertEquals(4, byteItems.size)

        val firstGroup = byteItems[0] as ByteGroup
        assertEquals(listOf("1A", "2B"), firstGroup.bytes)
        assertEquals(0, firstGroup.startIndex)
        assertEquals(1, firstGroup.endIndex)
        assertEquals("Early", firstGroup.definition.name)

        val middleSingle = byteItems[1] as SingleByte
        assertEquals(2, middleSingle.index)
        assertEquals("3C", middleSingle.value)

        val secondGroup = byteItems[2] as ByteGroup
        assertEquals(listOf("4D", "5E"), secondGroup.bytes)
        assertEquals(3, secondGroup.startIndex)
        assertEquals(4, secondGroup.endIndex)
        assertEquals("Late", secondGroup.definition.name)

        val lastSingle = byteItems[3] as SingleByte
        assertEquals(5, lastSingle.index)
        assertEquals("6F", lastSingle.value)
    }

    @Test
    fun toByteItemsWithStaticDefinitionPartiallyOutOfBoundsMarksGroupIncomplete() = runTest {
        // Given a 4-byte payload and a definition that extends beyond it
        val data = HexString("1A2B3C4D")
        val def = ByteGroupDefinition.createFromRange(2..5, "PartialGroup")

        // When
        val byteItems = data.toByteItems(listOf(def)).items

        // Then the group exists but is marked incomplete, containing only the in-bounds bytes
        val group = byteItems.filterIsInstance<ByteGroup>().firstOrNull()
        assertEquals(listOf("3C", "4D"), group?.bytes)
        assertEquals(true, group?.incomplete)
    }

    // endregion

    // region Variable formulas

    @Test
    fun toByteItemsResolvesVariableFormulasUsingLast() = runTest {
        // Given a 4-byte payload and a definition using [[LAST]] as end index
        val data = HexString("1A2B3C4D")
        val def = ByteGroupDefinition(
            name = "AllBytes",
            startFormula = "0",
            endFormula = "[[LAST]]",
        )

        // When
        val byteItems = data.toByteItems(listOf(def)).items

        // Then the group covers all bytes
        assertEquals(1, byteItems.size)
        val group = byteItems.first() as ByteGroup
        assertEquals(listOf("1A", "2B", "3C", "4D"), group.bytes)
        assertEquals(0, group.startIndex)
        assertEquals(3, group.endIndex)
    }

    @Test
    fun toByteItemsResolvesCrossDefinitionVariableFormulas() = runTest {
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
        val byteItems = data.toByteItems(listOf(lenDef, dataDef, restDef)).items

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

    @Test
    fun `toByteItems resolves start shortcut inside endFormula through the computation pipeline`() = runTest {
        // Given a definition where the end depends on the computed start of the same group
        val data = HexString("010203040506")
        val def = ByteGroupDefinition(
            name = "G",
            startFormula = "1",
            endFormula = "[[start]] + 2"
        )

        // When
        val byteItems = data.toByteItems(listOf(def)).items

        // Then - the group spans indices 1..3 (inclusive)
        assertEquals(4, byteItems.size)

        val firstSingle = byteItems[0] as SingleByte
        assertEquals(0, firstSingle.index)
        assertEquals("01", firstSingle.value)

        val group = byteItems[1] as ByteGroup
        assertEquals(listOf("02", "03", "04"), group.bytes)
        assertEquals(1, group.startIndex)
        assertEquals(3, group.endIndex)
        assertEquals("G", group.definition.name)

        val afterSingle1 = byteItems[2] as SingleByte
        assertEquals(4, afterSingle1.index)
        assertEquals("05", afterSingle1.value)

        val afterSingle2 = byteItems[3] as SingleByte
        assertEquals(5, afterSingle2.index)
        assertEquals("06", afterSingle2.value)
    }

    // endregion

    @Test
    fun `toByteItems resolves shortened start shortcut inside endFormula through the computation pipeline`() = runTest {
        // Given a definition where the end depends on the computed start of the same group
        val data = HexString("010203040506")
        val def = ByteGroupDefinition(
            name = "G",
            startFormula = "1",
            endFormula = "[[start]] + 2",
        )

        // When
        val byteItems = data.toByteItems(listOf(def)).items

        // Then the group spans indices 1..3 (inclusive)
        // Expected items: [Single(0), Group(1..3), Single(4), Single(5)]
        assertEquals(4, byteItems.size)

        val beforeGroup = byteItems[0] as SingleByte
        assertEquals(0, beforeGroup.index)
        assertEquals("01", beforeGroup.value)

        val group = byteItems[1] as ByteGroup
        assertEquals(listOf("02", "03", "04"), group.bytes)
        assertEquals(1, group.startIndex)
        assertEquals(3, group.endIndex)
        assertEquals("G", group.definition.name)

        val afterGroup1 = byteItems[2] as SingleByte
        assertEquals(4, afterGroup1.index)
        assertEquals("05", afterGroup1.value)

        val afterGroup2 = byteItems[3] as SingleByte
        assertEquals(5, afterGroup2.index)
        assertEquals("06", afterGroup2.value)
    }

    // region Error handling

    @Test
    fun toByteItemsRecordsErrorForUnresolvableFormula() = runTest {
        // Given a payload and a definition referencing an unknown variable
        val data = HexString("1A2B3C4D")
        val badDef = ByteGroupDefinition(
            name = "BadDef",
            startFormula = "[[UNKNOWN.start]]",
            endFormula = "[[UNKNOWN.end]]",
        )
        val goodDef = ByteGroupDefinition.createFromRange(0..1, "GoodDef")

        // When
        val result = data.toByteItems(listOf(badDef, goodDef))

        // Then
        assertEquals(1, result.items.filterIsInstance<ByteGroup>().size)
        assertTrue(result.errors.containsKey(badDef.id))
        assertTrue(result.errors[badDef.id]!!.contains("UNKNOWN"))
    }

    @Test
    fun toByteItemsRecordsOverlapErrors() = runTest {
        // Given a payload and overlapping group definitions
        val data = HexString("1A2B3C4D5E6F")
        val group1 = ByteGroupDefinition.createFromRange(0..2, "Group1")
        val group2 = ByteGroupDefinition.createFromRange(1..3, "Group2") // Overlaps

        // When
        val result = data.toByteItems(listOf(group1, group2))

        // Then
        assertEquals(1, result.items.filterIsInstance<ByteGroup>().size)
        assertTrue(result.errors.containsKey(group2.id))
        assertTrue(result.errors[group2.id]!!.contains("Overlap"))
    }

    @Test
    fun toByteItemsHandlesCircularDependencyByRecordingErrors() = runTest {
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

        // When
        val result = data.toByteItems(listOf(defA, defB))

        // Then
        assertTrue(result.items.all { it is SingleByte })
        assertTrue(result.errors.containsKey(defA.id) || result.errors.containsKey(defB.id))
    }

    @Test
    fun toByteItemsSkipsDefinitionWithUnresolvableFormulaWithoutCrashing() = runTest {
        // Given a payload and a definition referencing an unknown variable
        val data = HexString("1A2B3C4D")
        val badDef = ByteGroupDefinition(
            name = "BadDef",
            startFormula = "[[UNKNOWN.start]]",
            endFormula = "[[UNKNOWN.end]]",
        )
        val goodDef = ByteGroupDefinition.createFromRange(0..1, "GoodDef")

        // When - should not throw
        val byteItems = data.toByteItems(listOf(badDef, goodDef)).items

        // Then the bad definition is silently skipped; the good one is resolved
        val groups = byteItems.filterIsInstance<ByteGroup>()
        assertEquals(1, groups.size)
        assertEquals("GoodDef", groups.first().definition.name)
    }

    @Test
    fun toByteItemsHandlesCircularDependencyGracefullyWithoutCrashing() = runTest {
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
        val byteItems = data.toByteItems(listOf(defA, defB)).items

        // Then all bytes are single (registry throws → variables empty → formulas with [[ fail to resolve)
        assertTrue(byteItems.all { it is SingleByte })
    }

    // endregion

    // region Registry caching

    @Test
    fun toByteItemsReusesRegistryIfDefinitionsMatch() = runTest {
        // Given
        val data = HexString("1A2B3C")
        val definitions = listOf(ByteGroupDefinition.createFromRange(0..1, "G"))
        val registry = DefinitionVariableRegistry(definitions)

        // When
        val result = data.toByteItems(definitions, registry)

        // Then
        assertSame(registry, result.registry)
    }

    @Test
    fun toByteItemsRecreatesRegistryIfDefinitionsChange() = runTest {
        // Given
        val data = HexString("1A2B3C")
        val initialDefinitions = listOf(ByteGroupDefinition.createFromRange(0..1, "G1"))
        val newDefinitions = listOf(ByteGroupDefinition.createFromRange(0..1, "G2"))
        val registry = DefinitionVariableRegistry(initialDefinitions)

        // When
        val result = data.toByteItems(newDefinitions, registry)

        // Then
        assertNotSame(registry, result.registry)
        assertEquals(newDefinitions, result.registry.definitions)
    }

    // endregion
}
