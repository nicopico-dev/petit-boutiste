/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.calculator.Calculator
import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
import fr.nicopico.petitboutiste.calculator.models.VariableValues
import fr.nicopico.petitboutiste.models.data.DataString

/**
 * Result of validating a [ByteGroupDefinition]'s draft start/end formulas.
 */
data class FormulaValidation(
    val startError: String?,
    val endError: String?,
) {
    val isValid: Boolean get() = startError == null && endError == null
}

/**
 * Validates draft [startFormula]/[endFormula] the same way [fr.nicopico.petitboutiste.models.data.toByteItems]
 * resolves them, i.e. by expanding `[[start]]`/`[[end]]` shortcuts and resolving variables through [registry],
 * instead of naively replacing every `[[variable]]` reference with `0`.
 */
suspend fun ByteGroupDefinition.validateFormulas(
    boundaries: ByteGroupBoundaries,
    registry: DefinitionVariableRegistry,
    inputData: DataString,
): FormulaValidation {
    // Rebuild the registry with this definition's draft formulas so that any variable newly
    // referenced by the (not yet saved) draft is resolved the same way it would be once saved.
    val draftDefinition = copy(boundaries = boundaries)
    val definitionsForValidation = if (registry.definitions.any { it.id == id }) {
        registry.definitions.map { if (it.id == id) draftDefinition else it }
    } else {
        registry.definitions + draftDefinition
    }
    val draftRegistry = DefinitionVariableRegistry(definitionsForValidation)

    val variables = try {
        draftRegistry.computeVariableValues(inputData)
    } catch (error: Exception) {
        return FormulaValidation(
            startError = if (!boundaries.startFormula.isNullOrEmpty()) error.toErrorMessage() else null,
            endError = if (!boundaries.endFormula.isNullOrEmpty()) error.toErrorMessage() else null,
        )
    }

    val draft = draftDefinition.expandFormulas()

    // Validate based on which formulas are present
    val startValidationResult: FormulaValidationResult
    val startError: String?
    val endValidationResult: FormulaValidationResult
    val endError: String?

    if (draft.startFormula != null && draft.endFormula != null) {
        // start + end combination
        startValidationResult = validateFormula(
            formula = draft.startFormula,
            variables = variables,
            minValue = 0,
        )
        startError = when (startValidationResult) {
            is FormulaValidationResult.InvalidFormula -> startValidationResult.errorMessage
            else -> null
        }

        endValidationResult = validateFormula(
            formula = draft.endFormula,
            variables = variables,
            minValue = (startValidationResult as? FormulaValidationResult.ValidFormula)?.value ?: 0,
        )
        endError = when (endValidationResult) {
            is FormulaValidationResult.InvalidFormula -> endValidationResult.errorMessage
            else -> null
        }
    } else if (draft.startFormula != null && draft.lengthFormula != null) {
        // start + length combination
        startValidationResult = validateFormula(
            formula = draft.startFormula,
            variables = variables,
            minValue = 0,
        )
        startError = when (startValidationResult) {
            is FormulaValidationResult.InvalidFormula -> startValidationResult.errorMessage
            else -> null
        }

        endValidationResult = validateFormula(
            formula = draft.lengthFormula,
            variables = variables,
            minValue = 1,
        )
        endError = when (endValidationResult) {
            is FormulaValidationResult.InvalidFormula -> endValidationResult.errorMessage
            else -> null
        }
    } else if (draft.endFormula != null && draft.lengthFormula != null) {
        // end + length combination
        endValidationResult = validateFormula(
            formula = draft.endFormula,
            variables = variables,
            minValue = 0,
        )
        endError = when (endValidationResult) {
            is FormulaValidationResult.InvalidFormula -> endValidationResult.errorMessage
            else -> null
        }

        startValidationResult = validateFormula(
            formula = draft.lengthFormula,
            variables = variables,
            minValue = 1,
        )
        startError = when (startValidationResult) {
            is FormulaValidationResult.InvalidFormula -> startValidationResult.errorMessage
            else -> null
        }
    } else {
        return FormulaValidation(
            startError = "Invalid formula combination",
            endError = "Invalid formula combination"
        )
    }

    return FormulaValidation(startError, endError)
}

private fun validateFormula(
    formula: String,
    variables: VariableValues,
    minValue: Int,
): FormulaValidationResult {
    return if (formula.isEmpty()) {
        FormulaValidationResult.EmptyFormula
    } else {
        try {
            val computedValue = Calculator.computeOrThrow(formula, variables)
            if (computedValue < minValue) {
                FormulaValidationResult.InvalidFormula("Must be greater than or equal to $minValue")
            } else {
                FormulaValidationResult.ValidFormula(computedValue)
            }
        } catch (error: Exception) {
            FormulaValidationResult.InvalidFormula(error.toErrorMessage())
        }
    }
}

private sealed class FormulaValidationResult {
    data object EmptyFormula : FormulaValidationResult()
    data class ValidFormula(val value: Int) : FormulaValidationResult()
    data class InvalidFormula(val errorMessage: String) : FormulaValidationResult()
}

private fun Exception.toErrorMessage(): String = "Formula error: $message"
