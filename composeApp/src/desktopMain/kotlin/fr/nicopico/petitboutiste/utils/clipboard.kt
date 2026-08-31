/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
suspend fun Clipboard.setData(data: String): Boolean {
    val clipEntry = ClipEntry(StringSelection(data))
    try {
        setClipEntry(clipEntry)
        return true
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        logError("Failed to set clipboard", e)
        return false
    }
}
