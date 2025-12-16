/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.app

import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId
import fr.nicopico.petitboutiste.ui.theme.PBTheme

data class AppState(
    val tabs: List<TabData> = listOf(TabData(inputData = HexString("FF00"))),
    val selectedTabId: TabId = tabs.first().id,
    val appTheme: PBTheme = PBTheme.System,
) {
    init {
        require(selectedTabId in tabs.map { it.id }) {
            "`selectedTabId` $selectedTabId must be present in `tabs` ${tabs.joinToString { it.id.toString() }}"
        }
    }
}

val AppState.selectedTab: TabData
    get() = tabs.first { it.id == selectedTabId }
