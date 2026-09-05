/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.LocalOnAppEvent
import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.definition.createFullPayloadByteGroup
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.state.events.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.ui.components.definition.ByteGroupDefinitions
import fr.nicopico.petitboutiste.ui.components.foundation.DesktopScaffold
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabel
import fr.nicopico.petitboutiste.ui.components.foundation.PBTextArea
import fr.nicopico.petitboutiste.ui.components.representation.ByteItemRender
import fr.nicopico.petitboutiste.utils.compose.optionalSlot
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop

@Suppress("LongMethod")
@Composable
fun TabContent(
    inputData: DataString,
    definitions: List<ByteGroupDefinition>,
    byteItems: List<ByteItem>,
    errors: Map<String, String> = emptyMap(),
    scratchpad: String = "",
    defaultRepresentation: Representation = Representation(DataRenderer.Hexadecimal),
    variableRegistry: DefinitionVariableRegistry? = null,
) {
    val onCurrentTabEvent: (CurrentTabEvent) -> Unit = LocalOnAppEvent.current

    var selectedByteItem: ByteItem? by remember {
        mutableStateOf(null)
    }

    val fullPayload: ByteGroup? = remember(inputData, definitions, defaultRepresentation) {
        if (inputData.isNotEmpty() && definitions.isEmpty()) {
            createFullPayloadByteGroup(
                dataString = inputData,
                representation = defaultRepresentation,
            )
        } else null
    }

    // Ensure `selectedByteItem` is up to date
    LaunchedEffect(byteItems, definitions) {
        val update = when (val selectedByteItem = selectedByteItem) {
            is SingleByte -> {
                byteItems
                    .filterIsInstance<SingleByte>()
                    .firstOrNull {
                        it.index == selectedByteItem.index
                    }
            }
            is ByteGroup -> {
                byteItems
                    .filterIsInstance<ByteGroup>()
                    .firstOrNull {
                        it.definition.id == selectedByteItem.definition.id
                    }
            }
            null -> null
        }
        selectedByteItem = update
    }

    DesktopScaffold(
        main = {
            MainPane(
                inputData = inputData,
                byteItems = byteItems,
                onInputDataChanged = { data ->
                    onCurrentTabEvent(CurrentTabEvent.ChangeInputDataEvent(data))
                },
                selectedByteItem = selectedByteItem,
                onByteItemSelected = { selectedByteItem = it },
                onInputTypeChanged = { inputType ->
                    onCurrentTabEvent(CurrentTabEvent.ChangeInputTypeEvent(inputType))
                },
                onAddDefinition = { indexes ->
                    val definition = ByteGroupDefinition.createFromRange(
                        indexes = indexes,
                        representation = defaultRepresentation,
                    )
                    onCurrentTabEvent(CurrentTabEvent.AddDefinitionEvent(definition))
                },
                modifier = Modifier.padding(16.dp),
            )
        },
        side = {
            Column(Modifier.padding(16.dp)) {
                ByteGroupDefinitions(
                    definitions = definitions,
                    onAppendDefaultDefinition = {
                        onCurrentTabEvent(CurrentTabEvent.AppendDefaultDefinitionEvent)
                    },
                    onDuplicateDefinition = { definition ->
                        onCurrentTabEvent(CurrentTabEvent.DuplicateDefinitionEvent(definition))
                    },
                    onUpdateDefinition = { source, update ->
                        onCurrentTabEvent(CurrentTabEvent.UpdateDefinitionEvent(source, update))
                    },
                    onDeleteDefinition = { definition ->
                        onCurrentTabEvent(CurrentTabEvent.DeleteDefinitionEvent(definition))
                    },
                    selectedDefinition = (selectedByteItem as? ByteGroup)?.definition,
                    onDefinitionSelected = { definition ->
                        // Select the ByteGroup matching this definition
                        selectedByteItem = if (definition != null) {
                            byteItems.firstOrNull {
                                it is ByteGroup && it.definition == definition
                            }
                        } else null
                    },
                    byteItems = byteItems,
                    errors = errors,
                    variableRegistry = variableRegistry,
                    inputData = inputData,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(UiTags.BYTE_GROUP_DEFINITIONS),
                )

                Spacer(Modifier.height(16.dp))

                PBLabel("Scratchpad") {
                    PBTextArea(
                        value = scratchpad,
                        onValueChange = {
                            onCurrentTabEvent(CurrentTabEvent.UpdateScratchpadEvent(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .testTag(UiTags.SCRATCHPAD),
                    )
                }
            }
        },
        tools = (selectedByteItem ?: fullPayload).optionalSlot { renderedByteItem ->
            val useDefinitionRepresentation = renderedByteItem is ByteGroup
                && renderedByteItem in byteItems
            ByteItemRender(
                byteItem = renderedByteItem,
                representation = if (useDefinitionRepresentation) {
                    renderedByteItem.definition.representation
                } else defaultRepresentation,
                onRepresentationChanged = { representation ->
                    if (useDefinitionRepresentation) {
                        val currentDefinition = renderedByteItem.definition
                        if (representation != currentDefinition.representation) {
                            val updatedDefinition = currentDefinition.copy(representation = representation)
                            onCurrentTabEvent(CurrentTabEvent.UpdateDefinitionEvent(currentDefinition, updatedDefinition))
                        }
                    } else {
                        onCurrentTabEvent(CurrentTabEvent.UpdateDefaultRepresentationEvent(representation))
                    }
                },
                modifier = Modifier.padding(16.dp),
            )
        },
    )
}

@Preview
@Composable
private fun AppScreenPreview() {
    WrapForPreviewDesktop {
        TabContent(
            inputData = HexString(rawHexString = "33DAADDAAD"),
            definitions = emptyList(),
            byteItems = emptyList(),
            errors = emptyMap(),
        )
    }
}
