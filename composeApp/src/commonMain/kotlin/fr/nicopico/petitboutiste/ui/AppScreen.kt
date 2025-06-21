package fr.nicopico.petitboutiste.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.toByteItems
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import fr.nicopico.petitboutiste.ui.main.MainPane
import fr.nicopico.petitboutiste.ui.support.GroupManagementPane

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppScreen(
    inputData: HexString,
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    onDataChanged: (HexString) -> Unit,
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
            && (selectedByteItem as ByteItem.Group).definition !in groupDefinitions) {
            selectedByteItem = null
        }
    }

    MaterialTheme {
        SupportingPaneScaffold(
            directive = PaneScaffoldDirective.Default,
            value = scaffoldValue,
            paneExpansionState = rememberPaneExpansionState(),
            paneExpansionDragHandle = { state ->
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier =
                        Modifier
                            .paneExpansionDraggable(
                                state,
                                LocalMinimumInteractiveComponentSize.current,
                                interactionSource,
                                semanticsProperties = {},
                            )
                            .fillMaxHeight(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 8.dp)
                            .background(Color.LightGray)
                            .align(Alignment.Center)
                    )
                    Icon(
                        Icons.Filled.DragHandle,
                        "support pane drag handle",
                        modifier = Modifier
                            .rotate(90f)
                            .align(Alignment.Center)
                    )
                }
            },
            mainPane = {
                AnimatedPane(
                    Modifier
                        .safeContentPadding()
                        .padding(16.dp)
                ) {
                    MainPane(
                        inputData = inputData,
                        byteItems = byteItems,
                        onDataChanged = onDataChanged,
                        onByteItemClicked = {
                            selectedByteItem = if (selectedByteItem != it) it else null
                        },
                        selectedByteItem = selectedByteItem,
                    )
                }
            },
            supportingPane = {
                AnimatedPane(
                    Modifier
                        .safeContentPadding()
                        .padding(16.dp)
                ) {
                    GroupManagementPane(
                        groupDefinitions = groupDefinitions,
                        onGroupDefinitionsChanged = onGroupDefinitionsChanged,
                        onGroupDefinitionSelected = { definition ->
                            selectedByteItem = byteItems.firstOrNull {
                                it is ByteItem.Group && it.definition == definition
                            }
                        },
                        selectedGroup = selectedByteItem as? ByteItem.Group
                    )
                }
            },
        )
    }
}

@Preview
@Composable
private fun AppScreenPreview() {
    WrapForPreview {
        AppScreen(
            HexString(rawHexString = "33DAADDAAD"),
            onDataChanged = {},
            onGroupDefinitionsChanged = {}
        )
    }
}
