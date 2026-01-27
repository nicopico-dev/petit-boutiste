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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.LocalOnAppEvent
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.definition.createFullPayload
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.state.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.ui.components.definition.ByteGroupDefinitions
import fr.nicopico.petitboutiste.ui.components.foundation.DesktopScaffold
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabel
import fr.nicopico.petitboutiste.ui.components.foundation.PBTextArea
import fr.nicopico.petitboutiste.ui.components.representation.ByteItemRender
import fr.nicopico.petitboutiste.utils.compose.optionalSlot
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop

@Composable
fun TabContent(
    inputData: DataString,
    definitions: List<ByteGroupDefinition>,
    byteItems: List<ByteItem>,
    scratchpad: String = "",
) {
    val onCurrentTabEvent: (CurrentTabEvent) -> Unit = LocalOnAppEvent.current

    var selectedByteItem: ByteItem? by remember {
        mutableStateOf(null)
    }

    // As ByteItem.SingleByte do not have a definition, we use the same representation for all of them.
    // The same representation will be used for full-payload representation, when there is no definition.
    // (note that it is possible to create a ByteGroup with a single byte)
    var noDefinitionRepresentation by remember {
        mutableStateOf(Representation(DataRenderer.Off))
    }

    val fullPayload: ByteGroup? = remember(inputData, definitions, noDefinitionRepresentation) {
        if (inputData.isNotEmpty() && definitions.isEmpty()) {
            createFullPayload(
                dataString = inputData,
                representation = noDefinitionRepresentation,
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
                modifier = Modifier.padding(16.dp),
            )
        },
        side = {
            Column(Modifier.padding(16.dp)) {
                ByteGroupDefinitions(
                    definitions = definitions,
                    onAddDefinition = { definition ->
                        onCurrentTabEvent(CurrentTabEvent.AddDefinitionEvent(definition))
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
                    modifier = Modifier.weight(1f),
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
                    )
                }
            }
        },
        tools = (selectedByteItem ?: fullPayload).optionalSlot { renderedByteItem ->
            ByteItemRender(
                byteItem = renderedByteItem,
                representation = if (renderedByteItem is ByteGroup) {
                    renderedByteItem.definition.representation
                } else noDefinitionRepresentation,
                onRepresentationChanged = { representation ->
                    if (renderedByteItem is ByteGroup && renderedByteItem != fullPayload) {
                        val currentDefinition = renderedByteItem.definition
                        if (representation != currentDefinition.representation) {
                            val updatedDefinition = currentDefinition.copy(representation = representation)
                            onCurrentTabEvent(CurrentTabEvent.UpdateDefinitionEvent(currentDefinition, updatedDefinition))
                        }
                    } else {
                        noDefinitionRepresentation = representation
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
        )
    }
}
