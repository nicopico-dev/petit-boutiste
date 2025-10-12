package fr.nicopico.petitboutiste

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.selectedTab
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.getScreenCharacteristics
import fr.nicopico.petitboutiste.repository.AppStateRepository
import fr.nicopico.petitboutiste.repository.LegacyTemplateManager
import fr.nicopico.petitboutiste.repository.TemplateManager
import fr.nicopico.petitboutiste.repository.WindowStateRepository
import fr.nicopico.petitboutiste.ui.AppContent
import fr.nicopico.petitboutiste.ui.PBMenuBar
import fr.nicopico.petitboutiste.ui.PBTitleBar
import fr.nicopico.petitboutiste.ui.theme.PetitBoutisteTheme
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.jewel.ui.component.painterResource
import org.jetbrains.jewel.window.DecoratedWindow

private val windowStateRepository = WindowStateRepository()
private val appStateRepository = AppStateRepository()

private val reducer = Reducer(
    templateManager = TemplateManager(),
    legacyTemplateManager = LegacyTemplateManager(),
)

const val APP_ID = "fr.nicopico.petitboutiste"

fun main() {
    FileKit.init(appId = APP_ID)
    // Ensure File dialogs have as native appearance on macOS
    // https://filekit.mintlify.app/dialogs/file-picker#customizing-the-dialog
    System.setProperty("apple.awt.application.appearance", "system")

    application {
        val screenCharacteristics = getScreenCharacteristics()
        val windowState = rememberSaveable {
            windowStateRepository.restore(screenCharacteristics) ?: WindowState()
        }
        var appState by rememberSaveable {
            mutableStateOf(appStateRepository.restore())
        }
        val currentTab: TabData by remember {
            derivedStateOf { appState.selectedTab }
        }

        fun onEvent(event: AppEvent) {
            appState = reducer(appState, event)
        }

        PetitBoutisteTheme {
            DecoratedWindow(
                title = "Petit Boutiste",
                icon = painterResource("icons/app-icon.png"),
                onCloseRequest = {
                    windowStateRepository.save(windowState, screenCharacteristics)
                    appStateRepository.save(appState)
                    exitApplication()
                },
                state = windowState,
                content = {
                    PBMenuBar(currentTab, onEvent = ::onEvent)
                    PBTitleBar(appState, onEvent = ::onEvent)
                    AppContent(appState, onEvent = ::onEvent)
                }
            )
        }
    }
}
