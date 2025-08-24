package fr.nicopico.petitboutiste

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.ui.AppScreen
import fr.nicopico.petitboutiste.ui.TabBar
import fr.nicopico.petitboutiste.ui.theme.PetitBoutisteTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    appState: AppState,
    onAppEvent: (AppEvent) -> Unit,
) {
    val tabs = appState.tabs
    val selectedTabId = appState.selectedTabId

    val selectedTab by remember(appState) {
        derivedStateOf { tabs.first { it.id == selectedTabId } }
    }

    PetitBoutisteTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab bar for switching between tabs
            TabBar(
                tabs = tabs,
                selectedTabId = selectedTabId,
                onTabSelected = { tabId ->
                    onAppEvent(AppEvent.SelectTabEvent(tabId))
                },
                onTabAdded = {
                    onAppEvent(AppEvent.AddNewTabEvent)
                },
                onTabClosed = { tabId ->
                    onAppEvent(AppEvent.RemoveTabEvent(tabId))
                },
                onTabRenamed = { tabId, newName ->
                    onAppEvent(AppEvent.RenameTabEvent(tabId, newName))
                }
            )

            // Main app screen with the selected tab's data
            AppScreen(
                inputData = selectedTab.inputData,
                groupDefinitions = selectedTab.groupDefinitions,
                inputType = selectedTab.inputType,
                onInputDataChanged = { newData ->
                    onAppEvent(CurrentTabEvent.ChangeInputDataEvent(newData))
                },
                onGroupDefinitionsChanged = { newDefinitions ->
                    onAppEvent(CurrentTabEvent.ChangeDefinitionsEvent(newDefinitions))
                },
                onInputTypeChanged = { newInputType ->
                    onAppEvent(CurrentTabEvent.ChangeInputTypeEvent(newInputType))
                }
            )
        }
    }
}
