/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.emptyArgumentValues
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun RendererForm(
    arguments: List<DataRenderer.Argument>,
    values: ArgumentValues,
    onArgumentsChange: (ArgumentValues, submit: Boolean) -> Unit,
    showSubmitButton: Boolean,
    modifier: Modifier = Modifier,
) {
    var argumentValues: ArgumentValues = remember(arguments, values) { values }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        arguments.forEach { argument ->
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
                completeArguments = argumentValues,
                modifier = Modifier.fillMaxWidth()
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
        RendererForm(
            dataRenderer.arguments,
            values = emptyArgumentValues(),
            showSubmitButton = dataRenderer.requireUserValidation,
            onArgumentsChange = { _, _ -> },
            modifier = Modifier.padding(8.dp),
        )
    }
}
