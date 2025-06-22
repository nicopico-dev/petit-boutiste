@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)
package fr.nicopico.petitboutiste.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.extensions.toByteItems
import fr.nicopico.petitboutiste.ui.components.ByteItemContent
import fr.nicopico.petitboutiste.ui.components.DragHandle
import fr.nicopico.petitboutiste.ui.components.HexDisplay
import fr.nicopico.petitboutiste.ui.components.HexInput
import fr.nicopico.petitboutiste.ui.components.definition.ByteGroupDefinitions
import fr.nicopico.petitboutiste.ui.components.template.TemplateManagement
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@Composable
fun AppScreen(
    inputData: HexString,
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    onInputDataChanged: (HexString) -> Unit,
    onGroupDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
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
        if (selectedByteItem is ByteItem.Group
            && (selectedByteItem as ByteItem.Group).definition !in groupDefinitions
        ) {
            selectedByteItem = null
        }
    }

    MaterialTheme {
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
                    modifier = Modifier
                        .safeContentPadding()
                        .padding(16.dp),
                )
            },
            supportingPane = {
                SupportingPane(
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
                    modifier = Modifier
                        .safeContentPadding()
                        .padding(16.dp),
                )
            },
        )
    }
}

@Composable
private fun ThreePaneScaffoldPaneScope.MainPane(
    inputData: HexString,
    onInputDataChanged: (HexString) -> Unit,
    modifier: Modifier = Modifier,
    byteItems: List<ByteItem> = inputData.toByteItems(),
    selectedByteItem: ByteItem? = null,
    onByteItemSelected: (ByteItem?) -> Unit = {},
) {
    AnimatedPane(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Hex Input",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HexInput(
                value = inputData,
                onValueChange = { onInputDataChanged(it) },
                modifier = Modifier.heightIn(max = 120.dp)
            )

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
    definitions: List<ByteGroupDefinition>,
    onDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
    onDefinitionSelected: (ByteGroupDefinition?) -> Unit,
    modifier: Modifier = Modifier,
    selectedByteItem: ByteItem? = null,
) {
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
                modifier = Modifier.weight(1f)
            )

            if (selectedByteItem != null) {
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 4.dp),
                )

                ByteItemContent(
                    byteItem = selectedByteItem
                )
            }
        }
    }
}

@Preview
@Composable
private fun AppScreenPreview() {
    WrapForPreview {
        AppScreen(
            HexString(rawHexString = "33DAADDAAD"),
            onInputDataChanged = {},
            onGroupDefinitionsChanged = {}
        )
    }
}
