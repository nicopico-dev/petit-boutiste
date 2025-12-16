/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.jewel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange

/**
 * Keep state in sync with an external value without recreating it
 * to preserve the current selection
 */
fun TextFieldState.updateStateValue(value: String) {
    if (text.toString() != value) {
        val originalSelection = selection
        edit {
            replace(0, length, value)
            // Best-effort preserve selection if still valid
            val end = value.length
            val selStart = originalSelection.start.coerceIn(0, end)
            val selEnd = originalSelection.end.coerceIn(0, end)
            selection = TextRange(selStart, selEnd)
        }
    }
}
