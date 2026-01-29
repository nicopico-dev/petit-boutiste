/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.state.InputType
import org.jetbrains.jewel.ui.component.SegmentedControl
import org.jetbrains.jewel.ui.component.SegmentedControlButtonData
import org.jetbrains.jewel.ui.component.Text

@Composable
fun InputTypeToggle(
    current: InputType,
    onInputTypeChange: (InputType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Text("Input mode")

        val buttons = remember(current) {
            InputType.entries.map { inputType ->
                SegmentedControlButtonData(
                    selected = inputType == current,
                    content = { Text(inputType.label) },
                    onSelect = { onInputTypeChange(inputType) },
                )
            }
        }

        SegmentedControl(
            buttons = buttons
        )
    }
}

private val InputType.label: String
    get() = when (this) {
        InputType.HEX -> "HEX"
        InputType.BINARY -> "BIN"
        InputType.BASE64 -> "B64"
    }
