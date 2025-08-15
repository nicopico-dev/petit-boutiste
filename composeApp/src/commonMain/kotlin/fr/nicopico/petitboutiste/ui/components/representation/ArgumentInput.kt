package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.arguments.ArgValue
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.ui.components.foundation.Dropdown

@Composable
fun ArgumentInput(
    argument: DataRenderer.Argument,
    value: ArgValue?,
    onValueChanged: (ArgValue) -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    Column(modifier = modifier) {
        var textValue by remember(argument, value) {
            mutableStateOf(value ?: argument.defaultValue ?: "")
        }

        when (argument.type) {
            is ArgumentType.FileType -> {
                // TODO Display a file selection dialog for ArgumentType.FileType
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { value ->
                        textValue = value
                        onValueChanged(value)
                    },
                    label = { Text(argument.label) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ArgumentType.StringType -> {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { value ->
                        textValue = value
                        onValueChanged(value)
                    },
                    label = { Text(argument.label) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ArgumentType.ChoiceType<*> -> {
                with(argument.type) {
                    Dropdown(
                        label = argument.label,
                        items = choices,
                        selection = convert(textValue),
                        onItemSelected = { choice ->
                            onValueChanged(convertChoice(choice))
                        }
                    )
                }
            }
        }

    }
}
