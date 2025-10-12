package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.ui.components.TabContent

@Composable
fun AppContent(
    appState: AppState,
    onEvent: (AppEvent) -> Unit,
) {
    val selectedTab by remember(appState) {
        derivedStateOf {
            appState.tabs.first {
                it.id == appState.selectedTabId
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Main app screen with the selected tab's data
        TabContent(
            inputData = selectedTab.inputData,
            definitions = selectedTab.groupDefinitions,
            inputType = selectedTab.inputType,
            scratchpad = selectedTab.scratchpad,
            onCurrentTabEvent = { currentTabEvent ->
                onEvent(currentTabEvent)
            },
        )
    }
}
