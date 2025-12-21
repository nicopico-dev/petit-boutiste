/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.events.AppState

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
