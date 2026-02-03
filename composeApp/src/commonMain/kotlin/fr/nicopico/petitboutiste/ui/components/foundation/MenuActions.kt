/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import fr.nicopico.petitboutiste.LocalOnAppEvent
import fr.nicopico.petitboutiste.state.AppEvent
import fr.nicopico.petitboutiste.state.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.state.TabData
import fr.nicopico.petitboutiste.state.TabId
import fr.nicopico.petitboutiste.state.TabsState
import fr.nicopico.petitboutiste.utils.file.FileDialog
import fr.nicopico.petitboutiste.utils.file.FileDialogOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberMenuActions(
    tabsState: TabsState,
    onEvent: (AppEvent) -> Unit = LocalOnAppEvent.current
): MenuActions {
    val scope = rememberCoroutineScope()
    return remember(onEvent, scope, tabsState) { MenuActions(onEvent, scope, tabsState) }
}

class MenuActions(
    private val onEvent: (AppEvent) -> Unit,
    private val scope: CoroutineScope,
    private val tabsState: TabsState,
    private val fileDialog: FileDialog = FileDialog.Default,
) {
    fun addNewTab() {
        onEvent(AppEvent.AddNewTabEvent())
    }

    fun duplicateTab(tabId: TabId) {
        onEvent(AppEvent.DuplicateTabEvent(tabId))
    }

    fun removeTab(tabId: TabId) {
        val tabIndex = tabsState.tabs.indexOfFirst { it.id == tabId }
        if (tabIndex == -1) return
        val removedTab = tabsState.tabs[tabIndex]

        onEvent(AppEvent.RemoveTabEvent(tabId))
        onEvent(AppEvent.ShowSnackbarEvent(
            message = "Tab '${removedTab.name ?: "Untitled"}' removed",
            actionLabel = "Undo",
            onAction = {
                onEvent(AppEvent.UndoRemoveTabEvent(removedTab, tabIndex))
            }
        ))
    }

    fun loadTemplate() {
        scope.launch {
            fileDialog.show(
                title = "Load template",
                operation = FileDialogOperation.ChooseFile("json"),
                block = { selectedFile ->
                    onEvent(
                        CurrentTabEvent.LoadTemplateEvent(
                            selectedFile,
                            definitionsOnly = false,
                        )
                    )
                }
            )
        }
    }

    fun saveTemplate(tabData: TabData) {
        if (tabData.templateData != null) {
            onEvent(
                CurrentTabEvent.SaveTemplateEvent(
                    tabData.templateData.templateFile,
                    updateExisting = true
                )
            )
        } else {
            saveTemplateAs(tabData)
        }
    }

    fun saveTemplateAs(tabData: TabData) {
        scope.launch {
            fileDialog.show(
                title = "Save template as ...",
                operation = FileDialogOperation.CreateNewFile(
                    suggestedFilename = tabData.name ?: "Template",
                    extension = "json",
                ),
                block = { selectedFile ->
                    onEvent(CurrentTabEvent.SaveTemplateEvent(selectedFile, updateExisting = false))
                }
            )
        }
    }

    fun restoreDefinitions(tabData: TabData) {
        if (tabData.templateData == null) {
            // No-op
            return
        }
        onEvent(
            CurrentTabEvent.LoadTemplateEvent(
                tabData.templateData.templateFile,
                definitionsOnly = true,
            )
        )
    }

    fun addDefinitionsFromAnotherTemplate() {
        scope.launch {
            fileDialog.show(
                title = "Load definitions from...",
                operation = FileDialogOperation.ChooseFile("json"),
                block = { selectedFile ->
                    onEvent(CurrentTabEvent.AddDefinitionsFromTemplateEvent(selectedFile))
                }
            )
        }
    }

    fun clearAllDefinitions() {
        val currentTab = tabsState.selectedTab
        onEvent(CurrentTabEvent.ClearAllDefinitionsEvent)
        onEvent(AppEvent.ShowSnackbarEvent(
            message = "All definitions cleared",
            actionLabel = "Undo",
            onAction = {
                onEvent(CurrentTabEvent.UndoClearAllDefinitionsEvent(
                    tabId = currentTab.id,
                    rendering = currentTab.rendering,
                    templateData = currentTab.templateData
                ))
            }
        ))
    }
}
