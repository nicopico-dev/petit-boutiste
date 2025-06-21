package fr.nicopico.petitboutiste.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import fr.nicopico.petitboutiste.ui.main.MainPane
import fr.nicopico.petitboutiste.ui.support.GroupManagementPane

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppScreen(
    data: HexString,
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    onDataChanged: (HexString) -> Unit,
    onGroupDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
) {
    val scaffoldValue = ThreePaneScaffoldValue(
        primary = PaneAdaptedValue.Expanded,
        secondary = PaneAdaptedValue.Expanded,
        tertiary = PaneAdaptedValue.Hidden,
    )

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
                            ),
                ) {
                    Icon(
                        Icons.Filled.DragHandle,
                        "support pane drag handle",
                        modifier = Modifier.rotate(90f)
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
                        data = data,
                        groupDefinitions = groupDefinitions,
                        onDataChanged = onDataChanged,
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
                        onGroupDefinitionsChanged = onGroupDefinitionsChanged
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
