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
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.ui.components.foundation.Dropdown
import fr.nicopico.petitboutiste.ui.components.foundation.FileSelector

@Composable
fun ArgumentInput(
    argument: DataRenderer.Argument,
    value: ArgValue?,
    onValueChanged: (ArgValue?) -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    Column(modifier = modifier) {

        // Must be mutable to allow editing in the TextField below
        var argValue by remember(argument, value) {
            mutableStateOf(value ?: argument.defaultValue)
        }

        when (argument.type) {
            is FileType -> {
                FileSelector(
                    label = { Text(argument.label) },
                    onFileSelected = { file ->
                        onValueChanged(
                            file?.let { FileType.convertTo(it) }
                        )
                    },
                    selection = argValue?.let(FileType::convertFrom),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ArgumentType.StringType -> {
                OutlinedTextField(
                    value = argValue ?: "",
                    onValueChange = { value ->
                        argValue = value
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
                        selection = argValue?.let(::convertFrom),
                        onItemSelected = { choice ->
                            onValueChanged(convertChoice(choice))
                        }
                    )
                }
            }
        }

    }
}
