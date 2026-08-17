package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.expandFormulas
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ByteGroupDefinitionTest {

    @Test
    fun `expandFormulas should replace start shortcut in endFormula`() {
        val definition = ByteGroupDefinition(
            startFormula = "10",
            endFormula = "[[start]] + 5"
        )
        val expanded = definition.expandFormulas()
        assertEquals("10", expanded.startFormula)
        assertEquals("(10) + 5", expanded.endFormula)
    }

    @Test
    fun `expandFormulas should replace end shortcut in startFormula`() {
        val definition = ByteGroupDefinition(
            startFormula = "[[end]] - 5",
            endFormula = "20"
        )
        val expanded = definition.expandFormulas()
        assertEquals("(20) - 5", expanded.startFormula)
        assertEquals("20", expanded.endFormula)
    }

    @Test
    fun `expandFormulas should handle complex formulas`() {
        val definition = ByteGroupDefinition(
            startFormula = "[[end]] + 1",
            endFormula = "[[start]] + [[LEN.value]]"
        )
        val expanded = definition.expandFormulas()
        assertEquals("([[start]] + [[LEN.value]]) + 1", expanded.startFormula)
        assertEquals("([[end]] + 1) + [[LEN.value]]", expanded.endFormula)
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

    @Test
    fun `indexes must be ordered left to right`() {
        assertFails {
            ByteGroupDefinition.createFromRange(indexes = 4..3)
        }
    }
}
