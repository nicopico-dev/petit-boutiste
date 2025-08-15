package fr.nicopico.petitboutiste.repository

import androidx.compose.ui.window.WindowState

interface WindowStateRepository {

    fun save(windowState: WindowState)

    fun restore(): WindowState?

    companion object {
        private val instance by lazy {
            WindowStateRepositorySettings()
        }

        operator fun invoke(): WindowStateRepository = instance
    }
}
