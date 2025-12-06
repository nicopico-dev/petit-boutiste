package fr.nicopico.petitboutiste.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtTest {

    @Test
    fun `should increment an indexed text with underscore separator`() {
        // GIVEN
        val original = "COUNTER_1"

        // WHEN
        val actual = original.incrementIndexSuffix()

        // THEN
        assertEquals("COUNTER_2", actual)
    }

    @Test
    fun `should increment an indexed text with space separator`() {
        // GIVEN
        val original = "COUNTER 9"

        // WHEN
        val actual = original.incrementIndexSuffix()

        // THEN
        assertEquals("COUNTER 10", actual)
    }

    @Test
    fun `should increment an indexed text with dash separator`() {
        // GIVEN
        val original = "COUNTER-4"

        // WHEN
        val actual = original.incrementIndexSuffix()

        // THEN
        assertEquals("COUNTER-5", actual)
    }

    @Test
    fun `should add an index to non-indexed text`() {
        // GIVEN
        val original = "COUNTER"

        // WHEN
        val actual = original.incrementIndexSuffix()

        // THEN
        assertEquals("COUNTER 2", actual)
    }
}
