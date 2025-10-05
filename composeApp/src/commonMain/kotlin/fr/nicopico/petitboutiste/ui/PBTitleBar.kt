package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.models.app.selectedTab
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import fr.nicopico.petitboutiste.utils.file.FileDialogOperation
import fr.nicopico.petitboutiste.utils.file.showFileDialog
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.GlobalColors
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.intui.standalone.theme.light
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
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
    onEvent: (AppEvent) -> Unit,
) {
    val selectedTab by remember(appState) {
        derivedStateOf { appState.selectedTab }
    }

    TitleBar(modifier.newFullscreenControls()) {
        Row(
            Modifier.align(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TODO Dropdown is deprecated but ListComboBox is a bit ugly...
            Dropdown(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .height(50.dp)
                    .widthIn(min = 150.dp),
                menuContent = {
                    appState.tabs.forEach { tabData ->
                        selectableItem(
                            selected = tabData.id == appState.selectedTabId,
                            onClick = { onEvent(AppEvent.SelectTabEvent(tabData.id)) },
                            content = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    TabItem(tabData, Modifier.weight(1f))

                                    if (appState.tabs.size > 1) {
                                        Spacer(Modifier.width(16.dp))
                                        IconButton(
                                            content = {
                                                Icon(
                                                    key = AllIconsKeys.General.Close,
                                                    contentDescription = CLOSE_TAB_DESCRIPTION,
                                                    tint = JewelThemeUtils.colors.dangerousActionColor,
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
                        onClick = { onEvent(AppEvent.AddNewTabEvent) },
                        content = { Text("Add a new tab") },
                    )
                },
                content = { TabItem(selectedTab) },
            )

            TabToolbar(tabData = selectedTab, onEvent)

            Divider(
                orientation = Orientation.Vertical,
                color = JewelThemeUtils.colors.iconOnDarkTint,
                modifier = Modifier
                    .height(20.dp)
                    .padding(horizontal = 4.dp),
            )

            TemplateToolbar(tabData = selectedTab, onEvent)
        }
    }
}

@Composable
private fun TabItem(
    tabData: TabData,
    modifier: Modifier = Modifier,
) {
    with(tabData) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(name ?: "Untitled")

                if (templateData != null) {
                    Row {
                        Text(
                            text = templateData.templateFile.name,
                            maxLines = 1,
                            fontStyle = FontStyle.Italic,
                            color = JewelThemeUtils.colors.subTextColor,
                            style = TextStyle.Default.copy(fontSize = 12.sp)
                        )

                        if (templateData.definitionsHaveChanged) {
                            Text(
                                text = "*",
                                color = JewelThemeUtils.colors.subTextColor,
                                style = TextStyle.Default.copy(fontSize = 14.sp),
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
    onEvent: (AppEvent) -> Unit,
) {
    var showRenameDialog by remember { mutableStateOf(false) }

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
    onEvent: (AppEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()

    ToolbarItem(
        label = "Load template",
        iconKey = AllIconsKeys.General.OpenDisk,
        onClick = {
            // FIXME Factorize with PBMenuBar
            scope.launch {
                showFileDialog(
                    title = "Load template",
                    operation = FileDialogOperation.ChooseFile("json")
                ) { selectedFile ->
                    onEvent(CurrentTabEvent.LoadTemplateEvent(selectedFile))
                }
            }
        },
    )

    ToolbarItem(
        label = "Save template",
        iconKey = AllIconsKeys.Actions.MenuSaveall,
        onClick = {
            // FIXME Factorize with PBMenuBar
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
                        tint = JewelThemeUtils.colors.iconOnDarkTint,
                    )
                },
                modifier = Modifier.size(height = 35.dp, width = 30.dp),
                onClick = onClick
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun RenameTabDialog(
    currentName: String?,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val textFieldState = rememberTextFieldState(currentName ?: "")

    val submit = {
        val newTabName = textFieldState.text.toString()
        if (newTabName != currentName && newTabName.isNotBlank()) {
            onSubmit(newTabName)
        }
        onDismiss()
    }

    DialogWindow(
        onCloseRequest = onDismiss,
        title = "Rename",
        state = DialogState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(250.dp, 140.dp),
        ),
        content = {
            Column(
                Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    "Rename this tab",
                    style = JewelTheme.createDefaultTextStyle(
                        color = GlobalColors.light().text.normal
                    ),
                )

                TextField(
                    state = textFieldState,
                    placeholder = { Text("Tab Name") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = Done),
                    onKeyboardAction = { submit() }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.End),
                ) {
                    OutlinedButton(
                        content = { Text("Cancel") },
                        onClick = onDismiss,
                    )

                    DefaultButton(
                        content = { Text("Rename") },
                        onClick = submit
                    )
                }
            }
        }
    )
}
