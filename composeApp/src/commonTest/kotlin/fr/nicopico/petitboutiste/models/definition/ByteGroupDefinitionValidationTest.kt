/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.EndiannessArgument
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for [validateFormulas], focused on ensuring it resolves variables through the
 * real [DefinitionVariableRegistry] instead of naively replacing them with `0`.
 */
class ByteGroupDefinitionValidationTest {

    @Test
    fun `validateFormulas resolves variable references using their real computed value`() = runTest {
        // Given a payload where LEN=5, and a draft formula referencing [[LEN.value]] as its end
        // If variables were naively replaced with 0, "[[LEN.value]] + 1" would resolve to 1,
        // which is >= start (0), so it would wrongly appear valid regardless of the real value.
        // Here we use a start that is only valid if the real value (5) is used: "[[LEN.value]] - 1" (=4)
        val data = HexString("051A2B3C4D5E")
        val lenDef = ByteGroupDefinition(
            name = "LEN",
            startFormula = "0",
            endFormula = "0",
            representation = Representation(
                dataRenderer = DataRenderer.Integer,
                argumentValues = mapOf(EndiannessArgument.key to Endianness.BigEndian.name),
            ),
        )
        val registry = DefinitionVariableRegistry(listOf(lenDef))
        val draftDefinition = ByteGroupDefinition(startFormula = "1", endFormula = "1")

        // When validating a draft end formula depending on the real LEN value
        val validation = draftDefinition.validateFormulas(
            startFormula = "[[LEN.value]] - 1",
            endFormula = "[[LEN.value]] + 1",
            registry = registry,
            inputData = data,
        )

        // Then both formulas are valid because LEN.value correctly resolves to 5 (not 0)
        assertNull(validation.startError)
        assertNull(validation.endError)
        assertEquals(true, validation.isValid)
    }

    @Test
    fun `validateFormulas would wrongly fail if variables were replaced with 0 instead of the real value`() = runTest {
        // Given LEN=5, and a start formula that is only non-negative when using the real value:
        // real: 5 - 3 = 2 (valid) vs. naive "replace with 0": 0 - 3 = -3 (would be wrongly reported as invalid)
        val data = HexString("051A2B3C4D5E")
        val lenDef = ByteGroupDefinition(
            name = "LEN",
            startFormula = "0",
            endFormula = "0",
            representation = Representation(
                dataRenderer = DataRenderer.Integer,
                argumentValues = mapOf(EndiannessArgument.key to Endianness.BigEndian.name),
            ),
        )
        val registry = DefinitionVariableRegistry(listOf(lenDef))
        val draftDefinition = ByteGroupDefinition(startFormula = "1", endFormula = "1")

        // When validating a draft start formula depending on the real LEN value
        val validation = draftDefinition.validateFormulas(
            startFormula = "[[LEN.value]] - 3",
            endFormula = "10",
            registry = registry,
            inputData = data,
        )

        // Then the start formula is valid, proving the real value (5) was used instead of 0
        assertNull(validation.startError)
    }

    @Test
    fun `validateFormulas flags negative start index`() = runTest {
        // Given no group definitions
        val registry = DefinitionVariableRegistry(emptyList())
        val draftDefinition = ByteGroupDefinition(startFormula = "0", endFormula = "0")

        // When the start formula resolves to a negative number
        val validation = draftDefinition.validateFormulas(
            startFormula = "0 - 5",
            endFormula = "10",
            registry = registry,
            inputData = HexString(""),
        )

        // Then the start error is reported
        assertEquals("Must be a positive number", validation.startError)
    }

    @Test
    fun `validateFormulas flags end lower than start`() = runTest {
        // Given no group definitions
        val registry = DefinitionVariableRegistry(emptyList())
        val draftDefinition = ByteGroupDefinition(startFormula = "0", endFormula = "0")

        // When the end formula resolves to a value lower than the start
        val validation = draftDefinition.validateFormulas(
            startFormula = "5",
            endFormula = "2",
            registry = registry,
            inputData = HexString(""),
        )

        // Then the end error is reported
        assertEquals("Must be greater than or equal to Start", validation.endError)
    }

    @Test
    fun `validateFormulas flags invalid formula syntax`() = runTest {
        // Given no group definitions
        val registry = DefinitionVariableRegistry(emptyList())
        val draftDefinition = ByteGroupDefinition(startFormula = "0", endFormula = "0")

        // When the formulas cannot be parsed
        val validation = draftDefinition.validateFormulas(
            startFormula = "not a formula",
            endFormula = "also not a formula",
            registry = registry,
            inputData = HexString(""),
        )

        // Then both are reported as invalid
        assertEquals("Invalid formula", validation.startError)
        assertEquals("Invalid formula", validation.endError)
    }

    @Test
    fun `validateFormulas treats empty formulas as no error`() = runTest {
        // Given no group definitions
        val registry = DefinitionVariableRegistry(emptyList())
        val draftDefinition = ByteGroupDefinition(startFormula = "0", endFormula = "0")

        // When both formulas are empty (not yet filled in by the user)
        val validation = draftDefinition.validateFormulas(
            startFormula = "",
            endFormula = "",
            registry = registry,
            inputData = HexString(""),
        )

        // Then no errors are reported, but the overall validation is not automatically "valid"
        // (emptiness is handled by the caller, e.g. requiring both fields non-empty)
        assertNull(validation.startError)
        assertNull(validation.endError)
        assertEquals(true, validation.isValid)
    }
}
