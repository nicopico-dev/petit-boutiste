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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import fr.nicopico.petitboutiste.utils.preview.WrapForPreview
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.ui.component.Text

@Composable
fun TabContent(
    inputData: DataString,
    definitions: List<ByteGroupDefinition> = emptyList(),
    inputType: InputType = InputType.HEX,
    scratchpad: String = "",
    onCurrentTabEvent: (CurrentTabEvent) -> Unit = {},
) {
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

    // Ensure the definition is up to date for `selectedByteItem`
    LaunchedEffect(definitions) {
        if (selectedByteItem is ByteItem.Group) {
            val updatedDefinition = definitions.firstOrNull {
                it.indexes == (selectedByteItem as ByteItem.Group).definition.indexes
            }

            selectedByteItem = if (updatedDefinition != null) {
                (selectedByteItem as ByteItem.Group).copy(definition = updatedDefinition)
            } else null
        }
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
    WrapForPreview {
        Column {
            val labelTextStyle = JewelTheme.createDefaultTextStyle(fontWeight = FontWeight.Bold)

            // Preview with Hex input
            Text("Hex Input Preview", style = labelTextStyle)
            TabContent(
                HexString(rawHexString = "33DAADDAAD"),
                inputType = InputType.HEX
            )

            Spacer(Modifier.height(32.dp))

            // Preview with Binary input
            Text("Binary Input Preview", style = labelTextStyle)
            TabContent(
                HexString(rawHexString = "33DAADDAAD"),
                inputType = InputType.BINARY
            )
        }
    }
}
