/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

data class TabsState(
    val tabs: List<TabData>,
    val selectedTabId: TabId,
) {
    val selectedTabIndex: Int = tabs.indexOfFirst { it.id == selectedTabId }
    val selectedTab: TabData = tabs[selectedTabIndex]
}
