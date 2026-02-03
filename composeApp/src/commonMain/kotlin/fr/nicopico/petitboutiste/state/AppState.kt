/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.ui.theme.PBTheme

data class SnackbarState(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
)

data class AppState(
    val tabs: List<TabData> = listOf(
        TabData(
            rendering = TabDataRendering(
                inputData = HexString("FF00"),
            )
        )
    ),
    val selectedTabId: TabId = tabs.first().id,
    val appTheme: PBTheme = PBTheme.System,
    val snackbarState: SnackbarState? = null,
) {
    init {
        require(selectedTabId in tabs.map { it.id }) {
            "`selectedTabId` $selectedTabId must be present in `tabs` ${tabs.joinToString { it.id.toString() }}"
        }
    }
}

val AppState.selectedTab: TabData
    get() = tabs.first { it.id == selectedTabId }
