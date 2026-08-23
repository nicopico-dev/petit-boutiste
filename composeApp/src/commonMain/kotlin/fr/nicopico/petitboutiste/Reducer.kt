/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import fr.nicopico.petitboutiste.models.InputType
import fr.nicopico.petitboutiste.models.data.Base64String
import fr.nicopico.petitboutiste.models.data.BinaryString
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.data.toByteItems
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinitionSorter
import fr.nicopico.petitboutiste.models.definition.createDefinitionId
import fr.nicopico.petitboutiste.models.persistence.toTemplate
import fr.nicopico.petitboutiste.models.state.AppState
import fr.nicopico.petitboutiste.models.state.TabData
import fr.nicopico.petitboutiste.models.state.TabId
import fr.nicopico.petitboutiste.models.state.TabTemplateData
import fr.nicopico.petitboutiste.models.state.TabsState
import fr.nicopico.petitboutiste.models.state.events.AppEvent
import fr.nicopico.petitboutiste.repository.TemplateManager
import fr.nicopico.petitboutiste.utils.file.nameWithoutExtension
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.math.max

class Reducer(
    private val templateManager: TemplateManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    suspend operator fun invoke(state: AppState, event: AppEvent): AppState {
        return when (event) {
            is AppEvent.SwitchAppThemeEvent -> {
                state.copy(appTheme = event.appTheme)
            }

            is AppEvent.RefreshRenderingEvent -> {
                state.withTabsState {
                    copy(tabs = tabs.map { it.withUpdatedRendering() })
                }
            }

            //region Tab management
            is AppEvent.AddNewTabEvent -> {
                val newTab = (event.tabData ?: TabData()).withUpdatedRendering()
                state.withTabsState {
                    copy(
                        tabs = tabs + newTab,
                        selectedTabId = newTab.id,
                    )
                }
            }

            is AppEvent.SelectTabEvent -> {
                state.withTabsState {
                    copy(selectedTabId = event.tabId)
                }
            }

            is AppEvent.RenameTabEvent -> {
                state.updateTab(event.tabId) {
                    copy(name = event.tabName)
                }
            }

            is AppEvent.RemoveTabEvent -> {
                val tabs = state.tabsState.tabs
                    .filterNot { it.id == event.tabId }
                    .ifEmpty {
                        // Add a default tab if the last tab was closed
                        listOf(TabData().withUpdatedRendering())
                    }

                val selectedTabId = if (state.tabsState.selectedTabId == event.tabId) {
                    // Select the tab just before the deleted one, or the first tab
                    val nextSelectedTabIndex = max(
                        0,
                        state.tabsState.tabs.indexOfFirst { it.id == event.tabId } - 1,
                    )
                    tabs[nextSelectedTabIndex].id
                } else state.tabsState.selectedTabId

                state.withTabsState {
                    copy(tabs = tabs, selectedTabId = selectedTabId)
                }
            }

            is AppEvent.UndoRemoveTabEvent -> {
                val renderedTabData = event.tabData.withUpdatedRendering()
                val newTabs = state.tabsState.tabs.toMutableList()
                    .apply { add(event.index.coerceIn(0, size), renderedTabData) }

                state.withTabsState {
                    copy(tabs = newTabs, selectedTabId = event.tabData.id)
                }
            }

            is AppEvent.DuplicateTabEvent -> {
                // Copy the tab with a new ID to separate them
                val sourceTab = state.tabsState.tabs.firstOrNull { it.id == event.tabId }
                    ?: return state

                val duplicatedTab = sourceTab.copy(
                    id = TabId.create(),
                    name = sourceTab.name?.let { "$it (copy)" },
                ).withUpdatedRendering()
                val duplicateIndex = state.tabsState.tabs.indexOf(sourceTab) + 1

                val newTabs = state.tabsState.tabs
                    .toMutableList()
                    .apply {
                        add(duplicateIndex, duplicatedTab)
                    }
                    .toList()

                state.withTabsState {
                    copy(tabs = newTabs, selectedTabId = duplicatedTab.id)
                }
            }

            is AppEvent.CycleTabEvent -> {
                val currentIndex = state.tabsState.tabs.indexOf(state.tabsState.selectedTab)
                val nextIndex = when {
                    event.cycleForward && currentIndex == state.tabsState.tabs.lastIndex -> 0
                    event.cycleForward -> currentIndex + 1
                    currentIndex == 0 -> state.tabsState.tabs.lastIndex
                    else -> currentIndex - 1
                }
                val nextTab = state.tabsState.tabs[nextIndex]

                state.withTabsState {
                    copy(selectedTabId = nextTab.id)
                }
            }
            //endregion

            //region Current Tab
            is AppEvent.CurrentTabEvent.ChangeInputTypeEvent -> {
                state.updateCurrentTab {
                    val hexString = HexString(inputData.hexStringValue)
                    val updatedData: DataString = when (event.type) {
                        InputType.HEX -> hexString
                        InputType.BINARY -> BinaryString.fromHexString(hexString)
                        InputType.BASE64 -> Base64String.fromHexString(hexString)
                    }

                    copy(
                        rendering = rendering.copy(inputData = updatedData),
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.ChangeInputDataEvent -> {
                state.updateCurrentTab {
                    copy(
                        rendering = rendering.copy(inputData = event.data)
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.AddDefinitionEvent -> {
                state.updateCurrentTab {
                    copy(
                        rendering = rendering.copy(
                            groupDefinitions = (groupDefinitions + event.definition)
                                .sortedWith(ByteGroupDefinitionSorter),
                        ),
                        templateData = templateData?.copy(definitionsHaveChanged = true),
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.UpdateDefinitionEvent -> {
                state.updateCurrentTab {
                    val updatedDefinitions = groupDefinitions.map { definition ->
                        if (definition.id == event.sourceDefinition.id) event.updatedDefinition else definition
                    }

                    copy(
                        rendering = rendering.copy(
                            groupDefinitions = updatedDefinitions.sortedWith(ByteGroupDefinitionSorter),
                        ),
                        templateData = templateData?.copy(definitionsHaveChanged = true),
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.DeleteDefinitionEvent -> {
                state.updateCurrentTab {
                    copy(
                        rendering = rendering.copy(
                            groupDefinitions = groupDefinitions - event.definition,
                        ),
                        templateData = templateData?.copy(definitionsHaveChanged = true),
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.ClearAllDefinitionsEvent -> {
                state.updateCurrentTab {
                    copy(
                        rendering = rendering.copy(
                            groupDefinitions = emptyList(),
                        ),
                        templateData = null
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.UndoClearAllDefinitionsEvent -> {
                state.updateTab(event.tabId) {
                    copy(
                        rendering = event.rendering,
                        templateData = event.templateData,
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.UpdateScratchpadEvent -> {
                state.updateCurrentTab {
                    copy(
                        scratchpad = event.scratchpad,
                        templateData = templateData?.copy(
                            definitionsHaveChanged = true
                        )
                    )
                }
            }

            //region Templates
            is AppEvent.CurrentTabEvent.LoadTemplateEvent -> {
                val template = templateManager.loadTemplate(event.templateFilePath)

                state.updateCurrentTab {
                    copy(
                        rendering = rendering.copy(groupDefinitions = template.definitions),
                        scratchpad = if (event.definitionsOnly) {
                            // Keep current scratchpad
                            this.scratchpad
                        } else template.scratchpad,
                        templateData = TabTemplateData(event.templateFilePath),
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.SaveTemplateEvent -> {
                val template = with(state.tabsState.selectedTab) {
                    toTemplate(event.templateFilePath.nameWithoutExtension)
                }
                templateManager.saveTemplate(template, event.templateFilePath, event.updateExisting)

                state.updateCurrentTab {
                    copy(
                        templateData = TabTemplateData(event.templateFilePath)
                    ).withUpdatedRendering()
                }
            }

            is AppEvent.CurrentTabEvent.AddDefinitionsFromTemplateEvent -> {
                val template = templateManager.loadTemplate(event.templateFilePath)

                state.updateCurrentTab {
                    // Ensure incoming definitions have unique IDs
                    val currentIds = groupDefinitions.map { it.id }.toSet()
                    val newDefinitions = template.definitions.map {
                        if (it.id in currentIds) it.copy(id = createDefinitionId()) else it
                    }

                    copy(
                        rendering = rendering.copy(
                            groupDefinitions = groupDefinitions + newDefinitions,
                        )
                    ).withUpdatedRendering()
                }
            }
            //endregion

            //endregion
        }
    }

    private suspend fun TabData.withUpdatedRendering(): TabData {
        val result = rendering.inputData.toByteItems(
            groupDefinitions = rendering.groupDefinitions,
            registry = rendering.variableRegistry,
            dispatcher = dispatcher,
        )
        return copy(
            rendering = rendering.copy(
                byteItems = result.items,
                errors = result.errors,
                variableRegistry = result.registry,
            )
        )
    }

    companion object {
        private suspend inline fun AppState.updateCurrentTab(block: suspend TabData.() -> TabData): AppState {
            return updateTab(tabsState.selectedTabId, block)
        }

        private suspend inline fun AppState.updateTab(tabId: TabId, block: suspend TabData.() -> TabData): AppState {
            return copy(
                tabsState = tabsState.copy(
                    tabs = tabsState.tabs.map { tab ->
                        if (tab.id == tabId) tab.block() else tab
                    }
                )
            )
        }

        private suspend inline fun AppState.withTabsState(block: suspend TabsState.() -> TabsState): AppState {
            return copy(tabsState = tabsState.block())
        }
    }
}
