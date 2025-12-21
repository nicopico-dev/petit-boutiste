/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.calculator

import junit.framework.TestCase.assertEquals
import kotlin.test.Test
import kotlin.test.assertNull

class CalculatorTest {

    @Test
    fun `should compute formulas`() {
        assertEquals(3, compute("1 + 2"))
        assertEquals(4, compute("5 - 1"))
        assertEquals(2, compute("6 / 3"))
        assertEquals(8, compute("2 * 4"))
    }

    @Test
    fun `should compute complex formulas`() {
        assertEquals(7, compute("(5 * 2) - 3"))
    }

    @Test
    fun `should compute formulas without whitespace`() {
        assertEquals(3, compute("1+2"))
        assertEquals(4, compute("5-1"))
        assertEquals(2, compute("6/3"))
        assertEquals(8, compute("2*4"))
    }

    @Test
    fun `should convert simple integer`() {
        assertEquals(3, compute("3"))
        assertEquals(12, compute("12"))
    }

    @Test
    fun `should replace variables by their values`() {
        assertEquals(
            3,
            compute(
                "1 + [[LENGTH]]",
                mapOf("LENGTH" to 2)
            )
        )
        assertEquals(
            5,
            compute(
                "[[A]] + [[B]]",
                mapOf(
                    "A" to 2,
                    "B" to 3,

                )
            )
        )
    }

    @Test
    fun `should ignore invalid formulas`() {
        assertNull(compute("1 + 2a"))
        assertNull(compute("test"))
    }

}
