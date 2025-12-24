/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
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
        val viewModelStoreOwner = remember {
            object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            }
        }

        val lifecycleOwner = remember {
            object : LifecycleOwner {
                override val lifecycle: Lifecycle = LifecycleRegistry(this)
            }
        }

        LaunchedEffect(lifecycleOwner) {
            (lifecycleOwner.lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        DisposableEffect(viewModelStoreOwner, lifecycleOwner) {
            onDispose {
                (lifecycleOwner.lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                viewModelStoreOwner.viewModelStore.clear()
            }
        }

        CompositionLocalProvider(
            LocalViewModelStoreOwner provides viewModelStoreOwner,
            LocalLifecycleOwner provides lifecycleOwner,
        ) {
            PetitBoutiste(
                onAppClose = { exitApplication() },
            )
        }
    }
}
