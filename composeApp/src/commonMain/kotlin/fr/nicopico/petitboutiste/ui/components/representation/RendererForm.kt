package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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

@Composable
fun RendererForm(
    arguments: List<DataRenderer.Argument>,
    values: ArgumentValues,
    showSubmitButton: Boolean,
    onSubmit: (ArgumentValues) -> Unit,
    onArgumentsChangeWithoutSubmit: (ArgumentValues) -> Unit = {},
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
                value = argumentValues[argument.key],
                onValueChanged = { value ->
                    if (value != null) {
                        argumentValues[argument.key] = value
                    } else {
                        argumentValues.remove(argument.key)
                    }

                    if (showSubmitButton) {
                        // Do not submit the form automatically,
                        // but indicate that some parameters have changed
                        onArgumentsChangeWithoutSubmit(argumentValues)
                    } else {
                        onSubmit(argumentValues)
                    }
                },
                modifier = modifier.fillMaxWidth()
            )
        }

        if (showSubmitButton) {
            Button(
                content = {
                    Text("Render")
                },
                onClick = {
                    onSubmit(argumentValues)
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
            onSubmit = {}
        )
    }
}
