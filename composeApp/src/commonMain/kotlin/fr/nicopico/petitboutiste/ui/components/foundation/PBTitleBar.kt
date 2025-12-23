/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.LocalOnAppEvent
import fr.nicopico.petitboutiste.state.AppEvent
import fr.nicopico.petitboutiste.state.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.state.AppEvent.SwitchAppThemeEvent
import fr.nicopico.petitboutiste.state.AppState
import fr.nicopico.petitboutiste.state.TabData
import fr.nicopico.petitboutiste.state.selectedTab
import fr.nicopico.petitboutiste.ui.dialog.RenameTabDialog
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.PBIcons
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.utils.file.FileDialogOperation
import fr.nicopico.petitboutiste.utils.file.showFileDialog
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.icon.IconKey
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls

private const val CLOSE_TAB_DESCRIPTION = "Close tab"

@OptIn(ExperimentalFoundationApi::class, ExperimentalJewelApi::class)
@Composable
fun DecoratedWindowScope.PBTitleBar(
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    val selectedTab by remember(appState) {
        derivedStateOf { appState.selectedTab }
    }
    val selectedTabIndex by remember(appState, selectedTab) {
        derivedStateOf { appState.tabs.indexOf(selectedTab) }
    }
    val onEvent = LocalOnAppEvent.current

    TitleBar(
        modifier.newFullscreenControls().height(50.dp)
    ) {
        Row(
            Modifier.align(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                PBIcons.app,
                contentDescription = null,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            )

            // TODO Dropdown is deprecated but ListComboBox is a bit ugly...
            Dropdown(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(50.dp)
                    .widthIn(min = 150.dp),
                menuContent = {
                    appState.tabs.forEachIndexed { index, tabData ->
                        selectableItem(
                            selected = tabData.id == appState.selectedTabId,
                            onClick = { onEvent(AppEvent.SelectTabEvent(tabData.id)) },
                            content = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    TabItem(
                                        tabData,
                                        tabNum = index + 1,
                                        modifier = Modifier.weight(1f),
                                        fullChangeIndicator = true
                                    )

                                    if (appState.tabs.size > 1) {
                                        Spacer(Modifier.width(16.dp))
                                        IconButton(
                                            content = {
                                                Icon(
                                                    key = AllIconsKeys.General.Close,
                                                    contentDescription = CLOSE_TAB_DESCRIPTION,
                                                    tint = AppTheme.current.colors.dangerousActionColor,
                                                )
                                            },
                                            modifier = Modifier.size(20.dp),
                                            onClick = {
                                                // TODO Ask confirmation before closing the tab
                                                onEvent(AppEvent.RemoveTabEvent(tabData.id))
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }

                    selectableItem(
                        selected = false,
                        iconKey = AllIconsKeys.General.Add,
                        onClick = { onEvent(AppEvent.AddNewTabEvent()) },
                        content = { Text("Add a new tab") },
                    )
                },
                content = {
                    TabItem(
                        selectedTab,
                        tabNum = selectedTabIndex + 1,
                        Modifier.padding(end = 16.dp),
                    )
                },
            )

            TabToolbar(tabData = selectedTab)

            Divider(
                orientation = Orientation.Vertical,
                color = AppTheme.current.colors.titleBarIconTint,
                modifier = Modifier
                    .height(20.dp)
                    .padding(horizontal = 4.dp),
            )

            TemplateToolbar(tabData = selectedTab)
        }

        SwitchThemeButton(
            theme = appState.appTheme,
            onThemeSelected = { theme ->
                onEvent(SwitchAppThemeEvent(theme))
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp),
        )
    }
}

@Composable
private fun TabItem(
    tabData: TabData,
    tabNum: Int,
    modifier: Modifier = Modifier,
    fullChangeIndicator: Boolean = false,
) {
    with(tabData) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(name ?: "Untitled $tabNum")

                if (templateData != null) {
                    Row {
                        Text(
                            text = templateData.templateFile.name,
                            maxLines = 1,
                            fontStyle = FontStyle.Italic,
                            color = AppTheme.current.colors.subTextColor,
                            style = TextStyle.Default.copy(fontSize = 12.sp)
                        )

                        if (templateData.definitionsHaveChanged) {
                            Text(
                                text = if (fullChangeIndicator) "(Modified)" else "*",
                                color = AppTheme.current.colors.subTextColor,
                                style = TextStyle.Default.copy(
                                    fontSize = if (fullChangeIndicator) 12.sp else 14.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TabToolbar(
    tabData: TabData,
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    val onEvent = LocalOnAppEvent.current

    ToolbarItem(
        label = "Edit tab name",
        iconKey = AllIconsKeys.Actions.Edit,
        onClick = {
            showRenameDialog = true
        },
    )

    // Rename dialog
    if (showRenameDialog) {
        RenameTabDialog(
            currentName = tabData.name,
            onSubmit = {
                onEvent(AppEvent.RenameTabEvent(tabData.id, it))
                showRenameDialog = false
            },
            onDismiss = {
                showRenameDialog = false
            }
        )
    }
}

@Composable
private fun TemplateToolbar(
    tabData: TabData,
) {
    val scope = rememberCoroutineScope()
    val onEvent = LocalOnAppEvent.current

    ToolbarItem(
        label = "Load template",
        iconKey = AllIconsKeys.General.OpenDisk,
        onClick = {
            // TODO Factorize with PBMenuBar
            scope.launch {
                showFileDialog(
                    title = "Load template",
                    operation = FileDialogOperation.ChooseFile("json")
                ) { selectedFile ->
                    onEvent(
                        CurrentTabEvent.LoadTemplateEvent(
                            selectedFile,
                            definitionsOnly = false,
                        )
                    )
                }
            }
        },
    )

    ToolbarItem(
        label = "Save template",
        iconKey = AllIconsKeys.Actions.MenuSaveall,
        onClick = {
            // TODO Factorize with PBMenuBar
            if (tabData.templateData != null) {
                onEvent(
                    CurrentTabEvent.SaveTemplateEvent(
                        tabData.templateData.templateFile,
                        updateExisting = true
                    )
                )
            } else {
                scope.launch {
                    showFileDialog(
                        title = "Save new template",
                        operation = FileDialogOperation.CreateNewFile(
                            suggestedFilename = tabData.name ?: "Template",
                            extension = "json",
                        ),
                    ) { selectedFile ->
                        onEvent(CurrentTabEvent.SaveTemplateEvent(selectedFile, updateExisting = false))
                    }
                }
            }
        }
    )
}

@Composable
private fun SwitchThemeButton(
    theme: PBTheme,
    onThemeSelected: (PBTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nextTheme = run {
        val indexOfCurrent = PBTheme.entries.indexOf(theme)
        val indexOfNext = (indexOfCurrent + 1) % PBTheme.entries.size
        PBTheme.entries[indexOfNext]
    }

    fun currentThemeLabel(theme: PBTheme) = when (theme) {
        PBTheme.System -> "Follow system"
        PBTheme.Light -> "Light theme"
        PBTheme.Dark -> "Dark theme"
    }
    val nextThemeLabel = when (nextTheme) {
        PBTheme.System -> "follow system theme"
        PBTheme.Light -> "force light theme"
        PBTheme.Dark -> "force dark theme"
    }

    ToolbarItem(
        iconKey = when (theme) {
            PBTheme.System -> PBIcons.themeSystem
            PBTheme.Light -> PBIcons.themeLight
            PBTheme.Dark -> PBIcons.themeDark
        },
        label = "${currentThemeLabel(theme)}. Click to $nextThemeLabel",
        onClick = {
            onThemeSelected(nextTheme)
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ToolbarItem(
    label: String,
    iconKey: IconKey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Tooltip(
        tooltip = { Text(label) },
        content = {
            IconButton(
                content = {
                    Icon(
                        key = iconKey,
                        contentDescription = label,
                        tint = AppTheme.current.colors.titleBarIconTint,
                    )
                },
                modifier = Modifier.size(height = 35.dp, width = 30.dp),
                onClick = onClick
            )
        },
        modifier = modifier,
    )
}
