package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.models.definition.ByteGroupBoundaries
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.expandFormulas
import fr.nicopico.petitboutiste.models.definition.resolveIndexes
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class ByteGroupDefinitionTest {

    @Test
    fun `expandFormulas should replace start shortcut in endFormula`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas("10", "[[start]] + 5", null)
        )
        val expanded = definition.expandFormulas()
        assertEquals("10", expanded.startFormula)
        assertEquals("(10) + 5", expanded.endFormula)
    }

    @Test
    fun `expandFormulas should replace end shortcut in startFormula`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas("[[end]] - 5", "20", null)
        )
        val expanded = definition.expandFormulas()
        assertEquals("(20) - 5", expanded.startFormula)
        assertEquals("20", expanded.endFormula)
    }

    @Test
    fun `expandFormulas should handle complex formulas`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas("[[end]] + 1", "[[start]] + [[LEN.value]]", null)
        )
        val expanded = definition.expandFormulas()
        assertEquals("([[start]] + [[LEN.value]]) + 1", expanded.startFormula)
        assertEquals("([[end]] + 1) + [[LEN.value]]", expanded.endFormula)
    }

    @Test
    fun `expandFormulas should handle lengthFormula with start`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas("[[length]]", null, "5")
        )
        val expanded = definition.expandFormulas()
        assertEquals("5", expanded.startFormula)
        assertNull(expanded.endFormula)
        assertEquals("5", expanded.lengthFormula)
    }

    @Test
    fun `expandFormulas should handle lengthFormula with end`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas(null, "[[length]] + 10", "5")
        )
        val expanded = definition.expandFormulas()
        assertNull(expanded.startFormula)
        assertEquals("(5) + 10", expanded.endFormula)
        assertEquals("5", expanded.lengthFormula)
    }

    @Test
    fun `indexes must be positive integers`() {
        assertFails {
            ByteGroupDefinition.createFromRange(indexes = -1..2)
        }

        assertFails {
            ByteGroupDefinition.createFromRange(indexes = -3..-2)
        }
    }

    @Suppress("EmptyRange")
    @Test
    fun `indexes must be ordered left to right`() {
        assertFails {
            ByteGroupDefinition.createFromRange(indexes = 4..3)
        }
    }

    @Test
    fun `createFromStartAndLength should create valid definition`() {
        val definition = ByteGroupDefinition.createFromStartAndLength(
            start = 10,
            length = 5,
            name = "Test"
        )
        assertEquals("10", definition.startFormula)
        assertNull(definition.endFormula)
        assertEquals("5", definition.lengthFormula)
        assertEquals("Test", definition.name)
    }

    @Test
    fun `createFromEndAndLength should create valid definition`() {
        val definition = ByteGroupDefinition.createFromEndAndLength(
            end = 20,
            length = 10,
            name = "Test"
        )
        assertNull(definition.startFormula)
        assertEquals("20", definition.endFormula)
        assertEquals("10", definition.lengthFormula)
        assertEquals("Test", definition.name)
    }

    @Test
    fun `createFromStartAndLength should reject negative start`() {
        assertFails {
            ByteGroupDefinition.createFromStartAndLength(
                start = -1,
                length = 5
            )
        }
    }

    @Test
    fun `createFromStartAndLength should reject non-positive length`() {
        assertFails {
            ByteGroupDefinition.createFromStartAndLength(
                start = 0,
                length = 0
            )
        }
        assertFails {
            ByteGroupDefinition.createFromStartAndLength(
                start = 0,
                length = -1
            )
        }
    }

    @Test
    fun `createFromEndAndLength should reject negative end`() {
        assertFails {
            ByteGroupDefinition.createFromEndAndLength(
                end = -1,
                length = 5
            )
        }
    }

    @Test
    fun `createFromEndAndLength should reject non-positive length`() {
        assertFails {
            ByteGroupDefinition.createFromEndAndLength(
                end = 0,
                length = 0
            )
        }
        assertFails {
            ByteGroupDefinition.createFromEndAndLength(
                end = 0,
                length = -1
            )
        }
    }

    @Test
    fun `resolveIndexes should work with start and end`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas("10", "20", null)
        )
        val indexes = definition.resolveIndexes(emptyMap())
        assertEquals(10, indexes.first)
        assertEquals(20, indexes.last)
    }

    @Test
    fun `resolveIndexes should work with start and length`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas("10", null, "11")
        )
        val indexes = definition.resolveIndexes(emptyMap())
        assertEquals(10, indexes.first)
        assertEquals(20, indexes.last)
    }

    @Test
    fun `resolveIndexes should work with end and length`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas(null, "20", "11")
        )
        val indexes = definition.resolveIndexes(emptyMap())
        assertEquals(10, indexes.first)
        assertEquals(20, indexes.last)
    }

    @Test
    fun `ByteGroupBoundaries fromFormulas should reject invalid combinations`() {
        // Only startFormula
        assertFails {
            ByteGroupBoundaries.fromFormulas(startFormula = "10", endFormula = null, lengthFormula = null)
        }

        // Only endFormula
        assertFails {
            ByteGroupBoundaries.fromFormulas(startFormula = null, endFormula = "20", lengthFormula = null)
        }

        // Only lengthFormula
        assertFails {
            ByteGroupBoundaries.fromFormulas(startFormula = null, endFormula = null, lengthFormula = "10")
        }

        // All three
        assertFails {
            ByteGroupBoundaries.fromFormulas(startFormula = "10", endFormula = "20", lengthFormula = "11")
        }

        // None
        assertFails {
            ByteGroupBoundaries.fromFormulas(startFormula = null, endFormula = null, lengthFormula = null)
        }
    }

    @Test
    fun `ByteGroupBoundaries fromFormulas should accept valid combinations`() {
        // start + end
        val se = ByteGroupBoundaries.fromFormulas(startFormula = "10", endFormula = "20", lengthFormula = null)
        assert(se is ByteGroupBoundaries.StartAndEnd)

        // start + length
        val sl = ByteGroupBoundaries.fromFormulas(startFormula = "10", endFormula = null, lengthFormula = "11")
        assert(sl is ByteGroupBoundaries.StartAndLength)

        // end + length
        val el = ByteGroupBoundaries.fromFormulas(startFormula = null, endFormula = "20", lengthFormula = "11")
        assert(el is ByteGroupBoundaries.EndAndLength)
    }

    @Test
    fun `expandFormulas should handle length in start and end formulas`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas("[[length]]", "[[start]] + [[length]] - 1", null)
        )
        val expanded = definition.expandFormulas()
        assertEquals("([[end]] - [[start]] + 1)", expanded.startFormula)
        assertEquals("([[length]] - 1) + [[length]] - 1", expanded.endFormula)
    }

    @Test
    fun `ByteGroupDefinition should expose boundary properties`() {
        val definition = ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.StartAndEnd("10", "20"),
            name = "Test"
        )
        assertEquals("10", definition.startFormula)
        assertEquals("20", definition.endFormula)
        assertNull(definition.lengthFormula)
        assertEquals("Test", definition.name)
    }
}
