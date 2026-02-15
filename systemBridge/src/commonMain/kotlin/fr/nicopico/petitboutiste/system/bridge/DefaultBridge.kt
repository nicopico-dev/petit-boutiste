/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.system.bridge

import fr.nicopico.petitboutiste.system.SystemBridge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.skiko.SystemTheme

object DefaultBridge : SystemBridge {
    override fun log(msg: String) {
        // no-op
    }

    override fun observeThemeChanges(): Flow<SystemTheme> {
        return flowOf(SystemTheme.LIGHT)
    }
}
