/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.state.events

import fr.nicopico.petitboutiste.models.state.AppState
import fr.nicopico.petitboutiste.models.state.events.AppEvent.CurrentTabEvent

data class SnackbarEvent(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
)

/**
 * Get the [SnackbarEvent] corresponding to this [AppEvent].
 *
 * Returns `null` if the `AppEvent` does not require a snackbar
 */
fun AppEvent.getSnackbarEvent(
    previousState: AppState,
    onAppEvent: OnAppEvent,
): SnackbarEvent? {
    return when(this) {
        is CurrentTabEvent.ClearAllDefinitionsEvent -> {
            val selectedTab = previousState.tabsState.selectedTab
            SnackbarEvent(
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
            SnackbarEvent(
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
            SnackbarEvent(
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
