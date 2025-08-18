package fr.nicopico.petitboutiste.repository

import androidx.compose.ui.window.WindowState
import fr.nicopico.petitboutiste.models.ui.ScreenCharacteristics

interface WindowStateRepository {

    fun save(windowState: WindowState, screenCharacteristics: ScreenCharacteristics)

    fun restore(screenCharacteristics: ScreenCharacteristics): WindowState?

    companion object {
        private val instance by lazy {
            WindowStateRepositorySettings()
        }

        operator fun invoke(): WindowStateRepository = instance
    }
}
