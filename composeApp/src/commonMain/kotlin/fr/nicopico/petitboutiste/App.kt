package fr.nicopico.petitboutiste

import androidx.compose.runtime.Composable
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.ui.AppScreen
import fr.nicopico.petitboutiste.ui.theme.PetitBoutisteTheme

@Composable
fun App(
    appState: AppState,
    onAppEvent: (AppEvent) -> Unit,
) {
    val tabs = appState.tabs
    val selectedTabId = appState.selectedTabId

    PetitBoutisteTheme {
        AppScreen(tabs, selectedTabId, onAppEvent)
    }
}
