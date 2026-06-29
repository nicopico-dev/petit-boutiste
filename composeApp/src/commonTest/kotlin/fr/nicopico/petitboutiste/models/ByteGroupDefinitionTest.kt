package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import org.junit.Test
import kotlin.test.assertFails

class ByteGroupDefinitionTest {

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
