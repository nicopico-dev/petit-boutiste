package fr.nicopico.petitboutiste.models.app

import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId

data class AppState(
    val tabs: List<TabData> = listOf(TabData(inputData = HexString("FF00"))),
    val selectedTabId: TabId = tabs.first().id,
) {
    init {
        require(selectedTabId in tabs.map { it.id }) {
            "`selectedTabId` $selectedTabId must be present in `tabs` ${tabs.joinToString()}"
        }
    }
}
