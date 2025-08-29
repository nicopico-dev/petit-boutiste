package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.models.app.selectedTab
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls

private const val ADD_NEW_TAB_DESCRIPTION = "Add new tab"

@OptIn(ExperimentalFoundationApi::class, ExperimentalJewelApi::class)
@Composable
fun DecoratedWindowScope.PBTitleBar(
    appState: AppState,
    modifier: Modifier = Modifier,
    onEvent: (AppEvent) -> Unit,
) {
    val selectedTabIndex by remember {
        derivedStateOf { appState.tabs.indexOf(appState.selectedTab) }
    }

    TitleBar(modifier.newFullscreenControls()) {
        // TODO Show toolbar for most-used elements (load, save, clear)
        Row(Modifier.align(Alignment.Start)) {
            // TODO Dropdown is deprecated but ListComboBox is not satisfying...
            @Suppress("UnstableApiUsage")
            Dropdown(
                modifier = Modifier.height(30.dp).align(Alignment.CenterVertically),
                menuContent = {
                    appState.tabs.forEach { tabData ->
                        selectableItem(
                            selected = tabData.id == appState.selectedTabId,
                            onClick = { onEvent(AppEvent.SelectTabEvent(tabData.id)) },
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(tabData.name ?: "Untitled")
                                }
                            }
                        )
                    }
                },
                content = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(appState.selectedTab.name ?: "Untitled")
                        }
                    }
                },
            )

            Tooltip(
                tooltip = { Text(ADD_NEW_TAB_DESCRIPTION) },
                content = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = ADD_NEW_TAB_DESCRIPTION,
                                tint = JewelThemeUtils.darkIconTint
                            )
                        },
                        modifier = Modifier.size(40.dp).padding(5.dp),
                        onClick = { onEvent(AppEvent.AddNewTabEvent) }
                    )
                },
            )
        }

        Text(title)
    }
}
