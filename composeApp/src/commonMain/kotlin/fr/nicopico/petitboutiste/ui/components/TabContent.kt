package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
    onCurrentTabEvent: (CurrentTabEvent) -> Unit = {},
) {
    val byteItems = remember(inputData, definitions) {
        inputData.toByteItems(definitions)
    }

    var selectedByteItem: ByteItem? by remember {
        mutableStateOf(null)
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

    // As ByteItem.Single do not have a definition, we use the same representation for all of them
    // (note that it is possible to create a Group with a single byte)
    var singleByteRepresentation by remember {
        mutableStateOf(Representation(DataRenderer.Off))
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
        definitions = {
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
                modifier = Modifier.padding(16.dp),
            )
        },
        tools = selectedByteItem.optionalSlot { selectedByteItem ->
            ByteItemRender(
                byteItem = selectedByteItem,
                representation = if (selectedByteItem is ByteItem.Group) {
                    selectedByteItem.definition.representation
                } else singleByteRepresentation,
                onRepresentationChanged = { representation ->
                    if (selectedByteItem is ByteItem.Group) {
                        val currentDefinition = selectedByteItem.definition
                        if (representation != currentDefinition.representation) {
                            val updatedDefinition = currentDefinition.copy(representation = representation)
                            onCurrentTabEvent(CurrentTabEvent.UpdateDefinitionEvent(currentDefinition, updatedDefinition))
                        }
                    } else {
                        singleByteRepresentation = representation
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
