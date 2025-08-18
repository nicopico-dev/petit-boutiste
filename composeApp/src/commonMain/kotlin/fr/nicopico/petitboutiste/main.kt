package fr.nicopico.petitboutiste

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import fr.nicopico.petitboutiste.models.ui.getScreenCharacteristics
import fr.nicopico.petitboutiste.repository.WindowStateRepository

private val windowStateRepository = WindowStateRepository()

fun main() = application {
    val screenCharacteristics = getScreenCharacteristics()

    val windowState = rememberSaveable {
        windowStateRepository.restore(screenCharacteristics) ?: WindowState()
    }
    Window(
        title = "Petit Boutiste",
        state = windowState,
        onCloseRequest = {
            windowStateRepository.save(windowState, screenCharacteristics)
            exitApplication()
        },
        content = { App() },
    )
}
