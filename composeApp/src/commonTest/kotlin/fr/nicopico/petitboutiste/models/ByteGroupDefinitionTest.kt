package fr.nicopico.petitboutiste.models

import org.junit.Test
import kotlin.test.assertFails

class ByteGroupDefinitionTest {

    @Test
    fun `indexes must be positive integers`() {
        assertFails {
            ByteGroupDefinition(indexes = -1..2)
        }

        assertFails {
            ByteGroupDefinition(indexes = -3..-2)
        }
    }

    @Test
    fun `indexes must be ordered left to right`() {
        assertFails {
            ByteGroupDefinition(indexes = 4..3)
        }
    }
}
