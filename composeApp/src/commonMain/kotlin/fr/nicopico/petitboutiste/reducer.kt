package fr.nicopico.petitboutiste

import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId

fun reducer(state: AppState, event: AppEvent): AppState {
    log("Received event: $event on state: $state...")
    return when (event) {
        is AppEvent.AddNewTabEvent -> {
            state.copy(tabs = state.tabs + TabData())
        }

        is AppEvent.CurrentTabEvent.ChangeDefinitionsEvent -> {
            state.copy(
                tabs = state.tabs.update(state.selectedTabId) {
                    copy(groupDefinitions = event.definitions)
                }
            )
        }

        is AppEvent.CurrentTabEvent.ChangeInputDataEvent -> {
            state.copy(
                tabs = state.tabs.update(state.selectedTabId) {
                    copy(inputData = event.data)
                }
            )
        }

        is AppEvent.CurrentTabEvent.ChangeInputTypeEvent -> {
            state.copy(
                tabs = state.tabs.update(state.selectedTabId) {
                    copy(inputType = event.type)
                }
            )
        }

        is AppEvent.RemoveTabEvent -> {
            if (state.tabs.size > 1) {
                val tabs = state.tabs.filterNot { it.id == event.tabId }
                val selectedTabId = if (state.selectedTabId == event.tabId) {
                    tabs.first().id
                } else state.selectedTabId
                state.copy(tabs = tabs, selectedTabId = selectedTabId)
            } else state
        }

        is AppEvent.RenameTabEvent -> {
            state.copy(
                tabs = state.tabs.update(event.tabId) {
                    copy(name = event.tabName)
                }
            )
        }

        is AppEvent.SelectTabEvent -> state.copy(selectedTabId = event.tabId)
    }.also {
        log("  -> $it")
    }
}

private fun List<TabData>.update(tabId: TabId, block: TabData.() -> TabData): List<TabData> {
    return map { tab ->
        if (tab.id == tabId) tab.block() else tab
    }
}
