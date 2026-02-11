/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class IntRangeExtTest {

    @Test
    fun `should compute the size of an IntRange`() {
        // GIVEN
        val range = 1..3

        // WHEN
        val actualSize = range.size

        // THEN
        assertEquals(3, actualSize)
    }

    @Test
    fun `should compute the size of a single-element IntRange`() {
        // GIVEN
        val range = 1..1

        // WHEN
        val actualSize = range.size

        // THEN
        assertEquals(1, actualSize)
    }
}
