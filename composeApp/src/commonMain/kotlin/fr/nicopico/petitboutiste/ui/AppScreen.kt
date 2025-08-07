@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package fr.nicopico.petitboutiste.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.DataString
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.InputType
import fr.nicopico.petitboutiste.models.extensions.toByteItems
import fr.nicopico.petitboutiste.ui.components.BinaryInput
import fr.nicopico.petitboutiste.ui.components.ByteItemContent
import fr.nicopico.petitboutiste.ui.components.DragHandle
import fr.nicopico.petitboutiste.ui.components.HexDisplay
import fr.nicopico.petitboutiste.ui.components.HexInput
import fr.nicopico.petitboutiste.ui.components.definition.ByteGroupDefinitions
import fr.nicopico.petitboutiste.ui.components.template.TemplateManagement
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@Composable
fun AppScreen(
    inputData: DataString,
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    onInputDataChanged: (DataString) -> Unit,
    onGroupDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
    inputType: InputType = InputType.HEX,
    onInputTypeChanged: (InputType) -> Unit = {},
) {
    val scaffoldValue = ThreePaneScaffoldValue(
        primary = PaneAdaptedValue.Expanded,
        secondary = PaneAdaptedValue.Expanded,
        tertiary = PaneAdaptedValue.Hidden,
    )

    val byteItems = remember(inputData, groupDefinitions) {
        inputData.toByteItems(groupDefinitions)
    }

    var selectedByteItem: ByteItem? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(groupDefinitions) {
        if (selectedByteItem is ByteItem.Group) {
            val updatedDefinition = groupDefinitions.firstOrNull {
                it.indexes == (selectedByteItem as ByteItem.Group).definition.indexes
            }

            selectedByteItem = if (updatedDefinition != null) {
                (selectedByteItem as ByteItem.Group).copy(definition = updatedDefinition)
            } else null
        }
    }


    SupportingPaneScaffold(
        directive = PaneScaffoldDirective.Default,
        value = scaffoldValue,
        paneExpansionState = rememberPaneExpansionState(),
        paneExpansionDragHandle = { state ->
            DragHandle(state, "support pane drag handle")
        },
        mainPane = {
            MainPane(
                inputData = inputData,
                byteItems = byteItems,
                onInputDataChanged = onInputDataChanged,
                selectedByteItem = selectedByteItem,
                onByteItemSelected = { selectedByteItem = it },
                inputType = inputType,
                onInputTypeChanged = onInputTypeChanged,
                modifier = Modifier
                    .safeContentPadding()
                    .padding(16.dp)
                    // Need to add space to make the scrollbar handle grabbable
                    .padding(end = 16.dp),
            )
        },
        supportingPane = {
            SupportingPane(
                inputData = inputData,
                definitions = groupDefinitions,
                onDefinitionsChanged = onGroupDefinitionsChanged,
                onDefinitionSelected = { definition ->
                    // Select the ByteGroup matching this definition
                    selectedByteItem = if (definition != null) {
                        byteItems.firstOrNull {
                            it is ByteItem.Group && it.definition == definition
                        }
                    } else null
                },
                selectedByteItem = selectedByteItem,
                byteItems = byteItems,
                modifier = Modifier
                    .safeContentPadding()
                    .padding(16.dp),
            )
        },
    )
}

@Composable
private fun ThreePaneScaffoldPaneScope.MainPane(
    inputData: DataString,
    onInputDataChanged: (DataString) -> Unit,
    modifier: Modifier = Modifier,
    byteItems: List<ByteItem> = inputData.toByteItems(),
    selectedByteItem: ByteItem? = null,
    onByteItemSelected: (ByteItem?) -> Unit = {},
    inputType: InputType = InputType.HEX,
    onInputTypeChanged: (InputType) -> Unit = {},
) {
    AnimatedPane(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Data Input",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Input type selection
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Input Type:")
                RadioButton(
                    selected = inputType == InputType.HEX,
                    onClick = { onInputTypeChanged(InputType.HEX) }
                )
                Text("Hex")
                RadioButton(
                    selected = inputType == InputType.BINARY,
                    onClick = { onInputTypeChanged(InputType.BINARY) }
                )
                Text("Binary")
            }

            // Render the appropriate input component based on the selected input type
            when (inputType) {
                InputType.HEX -> HexInput(
                    value = inputData,
                    onValueChange = { onInputDataChanged(it) },
                    modifier = Modifier.heightIn(max = 120.dp)
                )
                InputType.BINARY -> BinaryInput(
                    value = inputData,
                    onValueChange = { onInputDataChanged(it) },
                    modifier = Modifier.heightIn(max = 120.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            HexDisplay(
                byteItems,
                selectedByteItem = selectedByteItem,
                modifier = Modifier.weight(1f),
                onByteItemClicked = {
                    onByteItemSelected(if (selectedByteItem != it) it else null)
                },
            )
        }
    }
}

@Composable
private fun ThreePaneScaffoldPaneScope.SupportingPane(
    inputData: DataString,
    definitions: List<ByteGroupDefinition>,
    onDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
    onDefinitionSelected: (ByteGroupDefinition?) -> Unit,
    modifier: Modifier = Modifier,
    selectedByteItem: ByteItem? = null,
    byteItems: List<ByteItem> = emptyList(),
) {
    var collapsedContent: Boolean by remember {
        mutableStateOf(false)
    }

    AnimatedPane(modifier) {
        Column {
            TemplateManagement(
                definitions = definitions,
                onTemplateLoaded = onDefinitionsChanged
            )

            HorizontalDivider(Modifier.padding(vertical = 4.dp))

            ByteGroupDefinitions(
                definitions = definitions,
                onDefinitionsChanged = onDefinitionsChanged,
                selectedDefinition = (selectedByteItem as? ByteItem.Group)?.definition,
                onDefinitionSelected = onDefinitionSelected,
                byteItems = byteItems,
                modifier = Modifier.weight(1f)
            )

            // If a byte item is selected, show its content
            // Otherwise, show the representation of the whole payload
            val byteItemToDisplay = selectedByteItem
                ?: if (inputData.isNotEmpty()) {
                    // Create a group representing the entire payload
                    ByteItem.Group(
                        index = 0,
                        bytes = inputData.hexString,
                        name = "Whole Payload"
                    )
                } else null

            if (byteItemToDisplay != null) {
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 4.dp),
                )

                ByteItemContent(
                    byteItem = byteItemToDisplay,
                    selectedRepresentation = (selectedByteItem as? ByteItem.Group)?.definition?.representation,
                    onRepresentationSelected = { newRepresentation ->
                        val group = selectedByteItem as? ByteItem.Group ?: return@ByteItemContent
                        val updatedDefinition = group.definition.copy(representation = newRepresentation)
                        val updatedDefinitions = definitions.map {
                            if (it == group.definition) updatedDefinition else it
                        }
                        onDefinitionsChanged(updatedDefinitions)
                    },
                    collapsed = collapsedContent,
                    onToggleCollapsed = { collapsedContent = it },
                )
            }
        }
    }
}

@Preview
@Composable
private fun AppScreenPreview() {
    WrapForPreview {
        Column {
            // Preview with Hex input
            Text("Hex Input Preview", style = MaterialTheme.typography.titleLarge)
            AppScreen(
                HexString(rawHexString = "33DAADDAAD"),
                onInputDataChanged = {},
                onGroupDefinitionsChanged = {},
                inputType = InputType.HEX
            )

            Spacer(Modifier.height(32.dp))

            // Preview with Binary input
            Text("Binary Input Preview", style = MaterialTheme.typography.titleLarge)
            AppScreen(
                HexString(rawHexString = "33DAADDAAD"),
                onInputDataChanged = {},
                onGroupDefinitionsChanged = {},
                inputType = InputType.BINARY
            )
        }
    }
}
