/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.fakes

import fr.nicopico.petitboutiste.repository.AppStateRepository
import fr.nicopico.petitboutiste.state.AppState

class FakeAppStateRepository : AppStateRepository {
    var savedState: AppState = AppState()
    override fun save(appState: AppState) {
        savedState = appState
    }
    override fun restore(): AppState = savedState
}
