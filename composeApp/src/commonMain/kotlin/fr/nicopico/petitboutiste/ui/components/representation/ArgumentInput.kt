package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.arguments.ArgValue
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.ui.components.foundation.PBDropdown
import fr.nicopico.petitboutiste.ui.components.foundation.PBFileSelector
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabel
import fr.nicopico.petitboutiste.ui.components.foundation.PBTextField
import fr.nicopico.petitboutiste.utils.compose.optionalSlot

@Composable
fun ArgumentInput(
    argument: DataRenderer.Argument,
    userValue: ArgValue?,
    onValueChanged: (ArgValue?) -> Unit,
    completeArguments: ArgumentValues,
    modifier: Modifier = Modifier.Companion,
) {
    val value: ArgValue? = remember(argument, userValue) {
        userValue ?: argument.defaultValue
    }

    Column(modifier = modifier) {
        PBLabel(
            label = argument.label,
            hint = argument.hint?.optionalSlot { hint ->
                ArgumentHint(hint)
            }
        ) {
            when (argument.type) {
                is FileType -> {
                    PBFileSelector(
                        onFileSelected = { file ->
                            onValueChanged(
                                file?.let { FileType.convertTo(it) }
                            )
                        },
                        selection = value?.let(FileType::convertFrom),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is ArgumentType.StringType -> {
                    PBTextField(
                        value = value ?: "",
                        onValueChange = onValueChanged,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                is ArgumentType.ChoiceType<*> -> {
                    with(argument.type) {
                        val choices by getChoices(completeArguments).collectAsStateWithLifecycle(emptyList())
                        // TODO Display a loading indicator while waiting for data to be loaded
                        PBDropdown(
                            items = choices,
                            selection = value?.let(::convertFrom),
                            onItemSelected = { choice ->
                                onValueChanged(convertChoice(choice))
                            }
                        )
                    }
                }
            }
        }
    }
}
