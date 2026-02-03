/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

import fr.nicopico.petitboutiste.state.AppEvent.CurrentTabEvent

fun AppEvent.getEventSnackbar(
    previousState: AppState,
    onAppEvent: OnAppEvent,
): SnackbarState? {
    return when(this) {
        is CurrentTabEvent.ClearAllDefinitionsEvent -> {
            val selectedTab = previousState.selectedTab
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
                message = "Definition ${(definition.name?.let { "'$it'" }.orEmpty())} deleted",
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
            val selectedTab = previousState.selectedTab
            SnackbarState(
                message = "Tab '${selectedTab.name ?: "Untitled"}' removed",
                actionLabel = "Undo",
                onAction = {
                    onAppEvent(
                        AppEvent.UndoRemoveTabEvent(
                            tabData = selectedTab,
                            index = previousState.tabs.indexOf(selectedTab),
                        )
                    )
                }
            )
        }

        else -> null
    }
}
