/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
