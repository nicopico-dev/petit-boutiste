/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ByteGroupDefinitionSorterTest {

    private val sorter = ByteGroupDefinitionSorter()

    @Test
    fun `compare constant formulas`() {
        val def1 = ByteGroupDefinition(startFormula = "10", endFormula = "20")
        val def2 = ByteGroupDefinition(startFormula = "5", endFormula = "15")

        assertTrue(sorter.compare(def1, def2) > 0)
        assertTrue(sorter.compare(def2, def1) < 0)
        assertEquals(0, sorter.compare(def1, def1))
    }

    @Test
    fun `compare arithmetic formulas without variables`() {
        val def1 = ByteGroupDefinition(startFormula = "5 + 3", endFormula = "10")
        val def2 = ByteGroupDefinition(startFormula = "2", endFormula = "3")

        // "5 + 3" resolves to 8, after 2
        assertTrue(sorter.compare(def1, def2) > 0)
        assertTrue(sorter.compare(def2, def1) < 0)
    }

    @Test
    fun `compare formulas using resolved variables`() {
        val sorterWithVariables = ByteGroupDefinitionSorter(mapOf("[[A.end]]" to 3))
        val defVariable = ByteGroupDefinition(startFormula = "[[A.end]] + 1", endFormula = "[[start]] + 5")
        val defConstant = ByteGroupDefinition(startFormula = "10", endFormula = "20")

        // "[[A.end]] + 1" resolves to 4, before 10
        assertTrue(sorterWithVariables.compare(defVariable, defConstant) < 0)
        assertTrue(sorterWithVariables.compare(defConstant, defVariable) > 0)
    }

    @Test
    fun `compare formulas using the end shortcut`() {
        val defShortcut = ByteGroupDefinition(startFormula = "[[end]] - 1", endFormula = "10")
        val defBefore = ByteGroupDefinition(startFormula = "8", endFormula = "8")
        val defAfter = ByteGroupDefinition(startFormula = "10", endFormula = "11")

        // "[[end]] - 1" resolves to 9
        assertTrue(sorter.compare(defShortcut, defBefore) > 0)
        assertTrue(sorter.compare(defShortcut, defAfter) < 0)
    }

    @Test
    fun `unresolvable formulas are sorted last`() {
        val defConstant = ByteGroupDefinition(startFormula = "10", endFormula = "20")
        val defUnresolvable = ByteGroupDefinition(startFormula = "[[PREV.end]] + 1", endFormula = "[[start]] + 5")

        assertTrue(sorter.compare(defConstant, defUnresolvable) < 0)
        assertTrue(sorter.compare(defUnresolvable, defConstant) > 0)
    }

    @Test
    fun `unresolvable formulas keep their relative order`() {
        val defVar1 = ByteGroupDefinition(startFormula = "[[PREV.end]] + 1", endFormula = "[[start]] + 5")
        val defVar2 = ByteGroupDefinition(startFormula = "[[OTHER.end]]", endFormula = "[[start]] + 2")

        assertEquals(0, sorter.compare(defVar1, defVar2))
    }

    @Test
    fun `sort definitions by resolved start value`() {
        val defUnresolvable = ByteGroupDefinition(startFormula = "[[UNKNOWN.end]]", endFormula = "[[start]]")
        val defLast = ByteGroupDefinition(startFormula = "[[LEN.value]] * 2", endFormula = "[[start]]")
        val defMiddle = ByteGroupDefinition(startFormula = "2 + 2", endFormula = "5")
        val defFirst = ByteGroupDefinition(startFormula = "0", endFormula = "1")

        val sorted = listOf(defUnresolvable, defLast, defMiddle, defFirst)
            .sortedWith(ByteGroupDefinitionSorter(mapOf("[[LEN.value]]" to 5)))

        assertEquals(listOf(defFirst, defMiddle, defLast, defUnresolvable), sorted)
    }
}
