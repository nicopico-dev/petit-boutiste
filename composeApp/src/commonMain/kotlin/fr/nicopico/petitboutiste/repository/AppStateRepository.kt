package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.app.AppState

interface AppStateRepository {

    fun save(appState: AppState)

    fun restore(): AppState

    companion object {
        private val instance: AppStateRepository by lazy {
            AppStateRepositorySettings()
        }

        operator fun invoke(): AppStateRepository = instance
    }
}
