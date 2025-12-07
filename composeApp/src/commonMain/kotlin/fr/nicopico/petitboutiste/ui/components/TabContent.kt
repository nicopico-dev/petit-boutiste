package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.LocalOnAppEvent
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.app.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.models.extensions.toByteItems
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.ui.components.definition.ByteGroupDefinitions
import fr.nicopico.petitboutiste.ui.components.foundation.DesktopScaffold
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabel
import fr.nicopico.petitboutiste.ui.components.foundation.PBTextArea
import fr.nicopico.petitboutiste.ui.components.representation.ByteItemRender
import fr.nicopico.petitboutiste.utils.compose.optionalSlot
import fr.nicopico.petitboutiste.utils.preview.WrapForPreviewDesktop

@Composable
fun TabContent(
    inputData: DataString,
    definitions: List<ByteGroupDefinition> = emptyList(),
    inputType: InputType = InputType.HEX,
    scratchpad: String = "",
) {
    val onCurrentTabEvent: (CurrentTabEvent) -> Unit = LocalOnAppEvent.current

    val byteItems = remember(inputData, definitions) {
        inputData.toByteItems(definitions)
    }

    var selectedByteItem: ByteItem? by remember {
        mutableStateOf(null)
    }

    // As ByteItem.Single do not have a definition, we use the same representation for all of them.
    // The same representation will be used for full-payload representation, when there is no definition.
    // (note that it is possible to create a Group with a single byte)
    var noDefinitionRepresentation by remember {
        mutableStateOf(Representation(DataRenderer.Off))
    }

    val fullPayload: ByteItem.Group? = remember(inputData, definitions, noDefinitionRepresentation) {
        if (inputData.isNotEmpty() && definitions.isEmpty()) {
            ByteItem.Group.createFullPayload(
                dataString = inputData,
                representation = noDefinitionRepresentation,
            )
        } else null
    }

    // Ensure `selectedByteItem` is up to date
    LaunchedEffect(inputData, definitions) {
        val update = when (val selectedByteItem = selectedByteItem) {
            is ByteItem.Single -> {
                byteItems
                    .filterIsInstance<ByteItem.Single>()
                    .firstOrNull {
                        it.index == selectedByteItem.index
                    }
            }
            is ByteItem.Group -> {
                byteItems
                    .filterIsInstance<ByteItem.Group>()
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
                inputType = inputType,
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
                    selectedDefinition = (selectedByteItem as? ByteItem.Group)?.definition,
                    onDefinitionSelected = { definition ->
                        // Select the ByteGroup matching this definition
                        selectedByteItem = if (definition != null) {
                            byteItems.firstOrNull {
                                it is ByteItem.Group && it.definition == definition
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
                representation = if (renderedByteItem is ByteItem.Group) {
                    renderedByteItem.definition.representation
                } else noDefinitionRepresentation,
                onRepresentationChanged = { representation ->
                    if (renderedByteItem is ByteItem.Group && renderedByteItem != fullPayload) {
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
            HexString(rawHexString = "33DAADDAAD"),
            inputType = InputType.HEX,
        )
    }
}
