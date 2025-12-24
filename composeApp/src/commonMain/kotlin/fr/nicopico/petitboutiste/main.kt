/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.compose.ui.window.application
import fr.nicopico.petitboutiste.ui.theme.system.followSystemTheme
import io.github.vinceglb.filekit.FileKit

const val APP_ID = "fr.nicopico.petitboutiste"

fun main() {
    FileKit.init(appId = APP_ID)
    // - Necessary for PBTheme.System
    // - Also ensure File dialogs have as native appearance on macOS
    //   https://filekit.mintlify.app/dialogs/file-picker#customizing-the-dialog
    followSystemTheme()

    application {
        PetitBoutiste(
            onAppClose = { exitApplication() },
        )
    }
}
