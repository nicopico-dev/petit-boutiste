/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import fr.nicopico.petitboutiste.utils.jewel.updateStateValue
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop

@Composable
fun observeTextFieldState(
    value: String,
    onValueChange: (String) -> Unit,
): TextFieldState {
    val state = remember {
        TextFieldState(value)
    }

    LaunchedEffect(value) {
        state.updateStateValue(value)
    }

    // Observe changes to the text
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .drop(1)
            .distinctUntilChanged()
            .collect {
                onValueChange(it)
            }
    }

    return state
}
