/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.events

import fr.nicopico.petitboutiste.models.events.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.models.state.AppState
import fr.nicopico.petitboutiste.models.state.SnackbarState

fun AppEvent.updateSnackbarState(
    previousState: AppState,
    onAppEvent: OnAppEvent,
): SnackbarState? {
    return when(this) {
        is CurrentTabEvent.ClearAllDefinitionsEvent -> {
            val selectedTab = previousState.tabsState.selectedTab
            SnackbarState(
                message = "All definitions cleared",
                actionLabel = "Undo",
                onAction = {
                    onAppEvent(
                        CurrentTabEvent.UndoClearAllDefinitionsEvent(
                            tabId = selectedTab.id,
                            rendering = selectedTab.rendering,
                            templateData = selectedTab.templateData,
                        )
                    )
                },
            )
        }

        is CurrentTabEvent.DeleteDefinitionEvent -> {
            SnackbarState(
                message = if (definition.name.isNullOrBlank()) {
                    "Definition deleted"
                } else "Definition '${definition.name}' deleted",
                actionLabel = "Undo",
                onAction = {
                    onAppEvent(
                        CurrentTabEvent.AddDefinitionEvent(
                            definition = this.definition
                        )
                    )
                }
            )
        }

        is AppEvent.RemoveTabEvent -> {
            val selectedTab = previousState.tabsState.selectedTab
            SnackbarState(
                message = "Tab '${selectedTab.name ?: "Untitled"}' removed",
                actionLabel = "Undo",
                onAction = {
                    onAppEvent(
                        AppEvent.UndoRemoveTabEvent(
                            tabData = selectedTab,
                            index = previousState.tabsState.tabs.indexOf(selectedTab),
                        )
                    )
                }
            )
        }

        else -> null
    }
}
