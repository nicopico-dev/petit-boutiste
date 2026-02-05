/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import fr.nicopico.petitboutiste.utils.logError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.timeout
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@Composable
fun ArgumentInput(
    argument: DataRenderer.Argument,
    userValue: ArgValue?,
    onValueChanged: (ArgValue?) -> Unit,
    completeArguments: ArgumentValues,
    modifier: Modifier = Modifier,
    onError: (String) -> Unit = {},
) {
    val value: ArgValue? = remember(argument, userValue) {
        userValue ?: argument.defaultValue
    }
    val completeArgumentsFlow = remember(argument, userValue) {
        MutableSharedFlow<ArgumentValues>(replay = 1)
    }
    LaunchedEffect(completeArgumentsFlow, completeArguments) {
        completeArgumentsFlow.emit(completeArguments)
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
                        val choices by getChoices(completeArgumentsFlow)
                            .flowOn(Dispatchers.Default)
                            .timeout(2.seconds)
                            .catch { error ->
                                logError("Error parsing choices for ${argument.key}", error)
                                onError("Error parsing choices for ${argument.label}")
                                emit(emptyList())
                            }
                            .collectAsStateWithLifecycle(null)

                        Box {
                            PBDropdown(
                                items = choices.orEmpty(),
                                selection = value?.let(::convertFrom),
                                modifier = Modifier.fillMaxWidth(),
                                onItemSelected = { choice ->
                                    onValueChanged(convertChoice(choice))
                                }
                            )

                            // Loading indicator while data is loading
                            if (choices == null) {
                                CircularProgressIndicator(
                                    Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(24.dp)
                                        .padding(end = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
