package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.arguments.ArgKey
import fr.nicopico.petitboutiste.models.representation.arguments.ArgValue
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.emptyArgumentValues
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun RendererForm(
    arguments: List<DataRenderer.Argument>,
    values: ArgumentValues,
    showSubmitButton: Boolean,
    onArgumentsChange: (ArgumentValues, submit: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val argumentValues = remember(arguments, values) {
        mutableStateMapOf<ArgKey, ArgValue>().also {
            it.putAll(values)
        }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        arguments.forEach { argument ->
            ArgumentInput(
                argument = argument,
                userValue = argumentValues[argument.key],
                onValueChanged = { value ->
                    if (value != null) {
                        argumentValues[argument.key] = value
                    } else {
                        argumentValues.remove(argument.key)
                    }

                    onArgumentsChange(
                        argumentValues,
                        !showSubmitButton, // Submit automatically if the form does not show a submit button
                    )
                },
                modifier = modifier.fillMaxWidth()
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

@Preview
@Composable
private fun RendererFormPreview() {
    WrapForPreview {
        RendererForm(
            DataRenderer.Protobuf.arguments,
            values = emptyArgumentValues(),
            showSubmitButton = true,
            onArgumentsChange = { _, _ -> },
        )
    }
}
