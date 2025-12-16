/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.app

import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.models.ByteGroupDefinitionSorter
import fr.nicopico.petitboutiste.models.persistence.toTemplate
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId
import fr.nicopico.petitboutiste.models.ui.TabTemplateData
import fr.nicopico.petitboutiste.repository.LegacyTemplateManager
import fr.nicopico.petitboutiste.repository.TemplateManager
import kotlinx.coroutines.runBlocking
import kotlin.math.max

class Reducer(
    private val templateManager: TemplateManager,
    private val legacyTemplateManager: LegacyTemplateManager,
) {

    // TODO Handle Concurrent changes to the AppState
    // TODO Remove usage of runBlocking
    operator fun invoke(state: AppState, event: AppEvent): AppState {
        log("Received event: $event...")
        return when (event) {
            is AppEvent.SwitchAppThemeEvent -> {
                state.copy(appTheme = event.appTheme)
            }

            //region Tab management
            is AppEvent.AddNewTabEvent -> {
                val newTab = event.tabData ?: TabData()
                state.copy(tabs = state.tabs + newTab, selectedTabId = newTab.id)
            }

            is AppEvent.SelectTabEvent -> {
                state.copy(selectedTabId = event.tabId)
            }

            is AppEvent.RenameTabEvent -> {
                state.copy(
                    tabs = state.tabs.update(event.tabId) {
                        copy(name = event.tabName)
                    }
                )
            }

            is AppEvent.RemoveTabEvent -> {
                val tabs = state.tabs
                    .filterNot { it.id == event.tabId }
                    .ifEmpty {
                        // Add a default tab if the last tab was closed
                        listOf(TabData())
                    }

                val selectedTabId = if (state.selectedTabId == event.tabId) {
                    // Select the tab just before the deleted one, or the first tab
                    val nextSelectedTabIndex = max(
                        0,
                        state.tabs.indexOfFirst { it.id == event.tabId } - 1,
                    )
                    tabs[nextSelectedTabIndex].id
                } else state.selectedTabId
                state.copy(tabs = tabs, selectedTabId = selectedTabId)
            }

            is AppEvent.DuplicateTabEvent -> {
                // Copy the tab with a new ID to separate them
                val sourceTab = state.tabs.firstOrNull { it.id == event.tabId }
                    ?: return state

                val duplicatedTab = sourceTab.copy(
                    id = TabId.create(),
                    name = sourceTab.name?.let { "$it (copy)" },
                )
                val duplicateIndex = state.tabs.indexOf(sourceTab) + 1

                val newTabs = state.tabs
                    .toMutableList()
                    .apply {
                        add(duplicateIndex, duplicatedTab)
                    }
                    .toList()

                state.copy(
                    tabs = newTabs,
                    selectedTabId = duplicatedTab.id,
                )
            }

            is AppEvent.CycleTabEvent -> {
                val currentIndex = state.tabs.indexOf(state.selectedTab)
                val nextIndex = when {
                    event.cycleForward && currentIndex == state.tabs.lastIndex -> 0
                    event.cycleForward -> currentIndex + 1
                    currentIndex == 0 -> state.tabs.lastIndex
                    else -> currentIndex - 1
                }

                val nextTab = state.tabs[nextIndex]
                state.copy(selectedTabId = nextTab.id)
            }
            //endregion

            //region Current Tab
            is AppEvent.CurrentTabEvent.ChangeInputTypeEvent -> {
                state.updateCurrentTab {
                    copy(inputType = event.type)
                }
            }

            is AppEvent.CurrentTabEvent.ChangeInputDataEvent -> {
                state.updateCurrentTab {
                    copy(inputData = event.data)
                }
            }

            is AppEvent.CurrentTabEvent.AddDefinitionEvent -> {
                state.updateCurrentTab {
                    copy(
                        groupDefinitions = (groupDefinitions + event.definition)
                            .sortedWith(ByteGroupDefinitionSorter),
                        templateData = templateData?.copy(definitionsHaveChanged = true),
                    )
                }
            }

            is AppEvent.CurrentTabEvent.UpdateDefinitionEvent -> {
                state.updateCurrentTab {
                    val updatedDefinitions = groupDefinitions.map { definition ->
                        if (definition.id == event.sourceDefinition.id) event.updatedDefinition else definition
                    }
                    copy(
                        groupDefinitions = updatedDefinitions.sortedWith(ByteGroupDefinitionSorter),
                        templateData = templateData?.copy(definitionsHaveChanged = true),
                    )
                }
            }

            is AppEvent.CurrentTabEvent.DeleteDefinitionEvent -> {
                state.updateCurrentTab {
                    copy(
                        groupDefinitions = groupDefinitions - event.definition,
                        templateData = templateData?.copy(definitionsHaveChanged = true),
                    )
                }
            }

            is AppEvent.CurrentTabEvent.ClearAllDefinitionsEvent -> {
                state.updateCurrentTab {
                    copy(groupDefinitions = emptyList(), templateData = null)
                }
            }

            is AppEvent.CurrentTabEvent.UpdateScratchpadEvent -> {
                state.updateCurrentTab {
                    copy(
                        scratchpad = event.scratchpad,
                    )
                }
            }

            //region Templates
            is AppEvent.CurrentTabEvent.LoadTemplateEvent -> {
                val template = runBlocking {
                    templateManager.loadTemplate(event.templateFile)
                }
                state.updateCurrentTab {
                    copy(
                        groupDefinitions = template.definitions,
                        templateData = TabTemplateData(event.templateFile),
                    )
                }
            }

            is AppEvent.CurrentTabEvent.SaveTemplateEvent -> {
                val template = with(state.selectedTab) {
                    toTemplate(event.templateFile.nameWithoutExtension)
                }
                runBlocking {
                    templateManager.saveTemplate(template, event.templateFile, event.updateExisting)
                }

                state.updateCurrentTab {
                    copy(templateData = TabTemplateData(event.templateFile))
                }
            }

            is AppEvent.CurrentTabEvent.AddDefinitionsFromTemplateEvent -> {
                val template = runBlocking {
                    templateManager.loadTemplate(event.templateFile)
                }
                state.updateCurrentTab {
                    // TODO Handle duplicate or conflicting definitions
                    copy(groupDefinitions = groupDefinitions + template.definitions)
                }
            }
            //endregion
            //endregion

            //region Legacy templates
            is AppEvent.ExportLegacyTemplatesEvent -> {
                runBlocking {
                    legacyTemplateManager.exportLegacyTemplates(event.exportFolder)
                }
                state
            }

            is AppEvent.ConvertLegacyTemplatesBundleEvent -> {
                runBlocking {
                    legacyTemplateManager.convertLegacyTemplates(event.bundleFile, event.exportFolder)
                }
                state
            }

            AppEvent.ClearAllLegacyTemplates -> {
                runBlocking {
                    legacyTemplateManager.deleteAllLegacyTemplates()
                }
                state
            }
            //endregion
        }.also {
            log("  -> $it")
        }
    }

    private fun List<TabData>.update(tabId: TabId, block: TabData.() -> TabData): List<TabData> {
        return map { tab ->
            if (tab.id == tabId) tab.block() else tab
        }
    }

    private fun AppState.updateCurrentTab(block: TabData.() -> TabData): AppState {
        return copy(tabs = tabs.update(selectedTabId, block))
    }
}
