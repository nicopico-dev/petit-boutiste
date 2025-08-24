package fr.nicopico.petitboutiste

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import fr.nicopico.petitboutiste.models.ui.getScreenCharacteristics
import fr.nicopico.petitboutiste.repository.AppStateRepository
import fr.nicopico.petitboutiste.repository.WindowStateRepository

private val windowStateRepository = WindowStateRepository()
private val appStateRepository = AppStateRepository()

fun main() = application {
    val screenCharacteristics = getScreenCharacteristics()

    val windowState = rememberSaveable {
        windowStateRepository.restore(screenCharacteristics) ?: WindowState()
    }
    var appState by rememberSaveable {
        mutableStateOf(appStateRepository.restore())
    }
    Window(
        title = "Petit Boutiste",
        state = windowState,
        onCloseRequest = {
            windowStateRepository.save(windowState, screenCharacteristics)
            appStateRepository.save(appState)
            exitApplication()
        },
        content = {
            App(
                appState = appState,
                onAppEvent = { event ->
                    appState = reducer(appState, event)
                }
            )
        },
    )
}
