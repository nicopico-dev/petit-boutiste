package fr.nicopico.petitboutiste.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.DataString
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.extensions.toByteItems
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.ui.components.DragHandle
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import fr.nicopico.petitboutiste.ui.panes.MainPane
import fr.nicopico.petitboutiste.ui.panes.SupportingPane

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
