/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ByteGroupDefinitionSorterTest {

    @Test
    fun `compare constant formulas`() {
        val def1 = ByteGroupDefinition(startFormula = "10", endFormula = "20")
        val def2 = ByteGroupDefinition(startFormula = "5", endFormula = "15")

        assertTrue(ByteGroupDefinitionSorter.compare(def1, def2) > 0)
        assertTrue(ByteGroupDefinitionSorter.compare(def2, def1) < 0)
        assertEquals(0, ByteGroupDefinitionSorter.compare(def1, def1))
    }

    @Test
    fun `compare constant and variable formulas`() {
        val defConstant = ByteGroupDefinition(startFormula = "10", endFormula = "20")
        val defVariable = ByteGroupDefinition(startFormula = "[[PREV.end]] + 1", endFormula = "[[start]] + 5")

        // Constants should come first
        assertTrue(ByteGroupDefinitionSorter.compare(defConstant, defVariable) < 0)
        assertTrue(ByteGroupDefinitionSorter.compare(defVariable, defConstant) > 0)
    }

    @Test
    fun `compare variable formulas`() {
        val defVar1 = ByteGroupDefinition(startFormula = "[[PREV.end]] + 1", endFormula = "[[start]] + 5")
        val defVar2 = ByteGroupDefinition(startFormula = "[[OTHER.end]]", endFormula = "[[start]] + 2")

        // Should keep relative order
        assertEquals(0, ByteGroupDefinitionSorter.compare(defVar1, defVar2))
    }
}
