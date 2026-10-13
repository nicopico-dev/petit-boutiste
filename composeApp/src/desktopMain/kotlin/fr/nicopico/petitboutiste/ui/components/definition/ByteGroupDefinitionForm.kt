/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.ByteGroupBoundaries
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.FormulaValidation
import fr.nicopico.petitboutiste.models.definition.validateFormulas
import fr.nicopico.petitboutiste.ui.UiTags
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabel
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabelOrientation.Horizontal
import fr.nicopico.petitboutiste.ui.components.foundation.PBTextField
import fr.nicopico.petitboutiste.ui.components.representation.ByteGroupRepresentationForm
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

private val fieldMaxWidth = 200.dp

@Suppress("LongMethod")
@Composable
fun ByteGroupDefinitionForm(
    definition: ByteGroupDefinition,
    onDefinitionSaved: (ByteGroupDefinition) -> Unit,
    variableRegistry: DefinitionVariableRegistry?,
    inputData: DataString,
    modifier: Modifier = Modifier,
    showRepresentationForm: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    var startFormulaInput by remember(definition.id) {
        mutableStateOf(definition.startFormula)
    }
    var endFormulaInput by remember(definition.id) {
        mutableStateOf(definition.endFormula)
    }
    var name by remember(definition.id) {
        mutableStateOf(definition.name.orEmpty())
    }
    var representation by remember(definition.id, definition.representation) {
        mutableStateOf(definition.representation)
    }

    //region Input validation
    val formulaValidation by produceState(
        initialValue = FormulaValidation(startError = null, endError = null),
        startFormulaInput,
        endFormulaInput,
        variableRegistry,
        inputData,
    ) {
        value = definition.validateFormulas(
            boundaries = ByteGroupBoundaries.fromFormulas(
                startFormula = startFormulaInput,
                endFormula = endFormulaInput,
                lengthFormula = null,
            ),
            registry = variableRegistry ?: DefinitionVariableRegistry(emptyList()),
            inputData = inputData,
        )
    }
    val startFormulaError = formulaValidation.startError
    val endFormulaError = formulaValidation.endError
    val isValid = startFormulaInput.isNotEmpty() && endFormulaInput.isNotEmpty() && formulaValidation.isValid
    //endregion

    val saveDefinition: () -> Unit = {
        if (isValid) {
            val definitionToSave = definition.copy(
                startFormula = startFormulaInput,
                endFormula = endFormulaInput,
                name = name.ifBlank { null },
                representation = representation,
            )
            onDefinitionSaved(definitionToSave)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        PBLabel("Name (optional)", orientation = Horizontal) {
            PBTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .widthIn(max = fieldMaxWidth)
                    .fillMaxWidth()
                    .testTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_NAME),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                onKeyboardAction = { focusManager.moveFocus(FocusDirection.Next) },
            )
        }

        PBLabel("Start", orientation = Horizontal) {
            PBTextField(
                value = startFormulaInput,
                onValueChange = { startFormulaInput = it },
                isError = startFormulaError != null,
                errorText = startFormulaError,
                modifier = Modifier
                    .widthIn(max = fieldMaxWidth)
                    .fillMaxWidth()
                    .testTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_START),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                onKeyboardAction = { focusManager.moveFocus(FocusDirection.Next) },
            )
        }

        PBLabel("End", orientation = Horizontal) {
            val isLastInput = representation.dataRenderer.arguments.isEmpty()
            PBTextField(
                value = endFormulaInput,
                onValueChange = { endFormulaInput = it },
                isError = endFormulaError != null,
                errorText = endFormulaError,
                modifier = Modifier
                    .widthIn(max = fieldMaxWidth)
                    .fillMaxWidth()
                    .testTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_END),
                keyboardOptions = KeyboardOptions(
                    imeAction = if (isLastInput) ImeAction.Done else ImeAction.Next
                ),
                onKeyboardAction = {
                    if (isLastInput) {
                        saveDefinition()
                    } else {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                },
            )
        }

        if (showRepresentationForm) {
            ByteGroupRepresentationForm(
                representation = representation,
                onRepresentationChanged = { representation = it },
                modifier = Modifier
                    .testTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_REPRESENTATION_FORM),
            )
        }

        DefaultButton(
            content = { Text(text = "Save definition") },
            onClick = saveDefinition,
            enabled = isValid,
            modifier = Modifier
                .align(Alignment.End)
                .testTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_SAVE),
        )
    }
}

@Preview
@Composable
private fun ByteGroupDefinitionFormPreview() {
    WrapForPreviewDesktop {
        ByteGroupDefinitionForm(
            definition = ByteGroupDefinition.createFromRange(
                indexes = 2..5,
                name = "Test ByteGroup"
            ),
            onDefinitionSaved = {},
            variableRegistry = null,
            inputData = HexString(""),
        )
    }
}
