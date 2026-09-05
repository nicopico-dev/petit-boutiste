/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.persistence

import androidx.compose.runtime.Stable
import fr.nicopico.petitboutiste.models.state.events.OnAppEvent
import kotlinx.io.files.Path

typealias OnFileReady = (Path) -> Unit

@Stable
data class NewFileOptions(
    val suggestedFileName: String,
    val extension: String,
    val onFileSelected: (OnAppEvent, Path, OnFileReady) -> Unit,
)
