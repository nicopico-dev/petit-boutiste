/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.LocalOnSnackbar
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.emptyArgumentValues
import fr.nicopico.petitboutiste.state.SnackbarState
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun RendererArgumentsForm(
    arguments: List<DataRenderer.Argument>,
    values: ArgumentValues,
    onArgumentsChange: (ArgumentValues, submit: Boolean) -> Unit,
    showSubmitButton: Boolean,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val onSnackbar = LocalOnSnackbar.current
    var argumentValues: ArgumentValues = remember(arguments, values) { values }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        arguments.forEachIndexed { index, argument ->
            val isLast = index == arguments.lastIndex
            ArgumentInput(
                argument = argument,
                userValue = argumentValues[argument.key],
                onValueChanged = { value ->
                    argumentValues = if (value != null) {
                        argumentValues + (argument.key to value)
                    } else {
                        argumentValues - argument.key
                    }

                    onArgumentsChange(
                        argumentValues,
                        // Submit automatically if the form does not show a Submit button
                        !showSubmitButton,
                    )
                },
                onError = { message ->
                    onSnackbar(SnackbarState(message))
                },
                completeArguments = argumentValues,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = if (isLast && !showSubmitButton) ImeAction.Done else ImeAction.Next
                ),
                onKeyboardAction = {
                    if (isLast && showSubmitButton) {
                        onArgumentsChange(argumentValues, true)
                    } else {
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                },
            )
        }

        if (showSubmitButton) {
            DefaultButton(
                content = {
                    Text("Render")
                },
                onClick = {
                    onArgumentsChange(argumentValues, true)
                },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

private object DataRenderParameterProvider : PreviewParameterProvider<DataRenderer> {
    override val values: Sequence<DataRenderer> = sequenceOf(
        DataRenderer.Hexadecimal,
        DataRenderer.Text,
        DataRenderer.Protobuf,
        DataRenderer.SubTemplate,
    )
}

@Preview
@Composable
private fun RendererFormPreview() {
    WrapForPreviewDesktop(DataRenderParameterProvider) { dataRenderer ->
        RendererArgumentsForm(
            dataRenderer.arguments,
            values = emptyArgumentValues(),
            showSubmitButton = dataRenderer.requireUserValidation,
            onArgumentsChange = { _, _ -> },
            modifier = Modifier.padding(8.dp),
        )
    }
}
