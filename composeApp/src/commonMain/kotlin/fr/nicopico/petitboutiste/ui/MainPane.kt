/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.state.InputType
import fr.nicopico.petitboutiste.ui.components.data.HexDisplay
import fr.nicopico.petitboutiste.ui.components.data.input.InputTypeToggle
import fr.nicopico.petitboutiste.ui.components.data.input.base64.Base64Input
import fr.nicopico.petitboutiste.ui.components.data.input.binary.BinaryInput
import fr.nicopico.petitboutiste.ui.components.data.input.hexadecimal.HexInput
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.typography
import org.jetbrains.jewel.ui.component.Text

@Composable
fun MainPane(
    inputData: DataString,
    byteItems: List<ByteItem>,
    onInputDataChanged: (DataString) -> Unit,
    modifier: Modifier = Modifier.Companion,
    selectedByteItem: ByteItem? = null,
    onByteItemSelected: (ByteItem?) -> Unit = {},
    onInputTypeChanged: (InputType) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = "Data Input",
                style = AppTheme.current.typography.title,
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Center)
            )

            InputTypeToggle(
                inputData.inputType,
                onInputTypeChanged,
                Modifier.align(Alignment.CenterEnd)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Render the appropriate input component based on the selected input type
        val inputModifier = Modifier
            .heightIn(max = 120.dp)
            .fillMaxWidth()
        when (inputData.inputType) {
            InputType.HEX -> HexInput(
                value = inputData,
                onValueChange = { onInputDataChanged(it) },
                modifier = inputModifier
            )

            InputType.BINARY -> BinaryInput(
                value = inputData,
                onValueChange = { onInputDataChanged(it) },
                modifier = inputModifier
            )

            InputType.BASE64 -> Base64Input(
                value = inputData,
                onValueChange = { onInputDataChanged(it) },
                modifier = inputModifier
            )
        }

        Spacer(Modifier.height(16.dp))

        HexDisplay(
            byteItems = byteItems,
            selectedByteItem = selectedByteItem,
            modifier = Modifier.weight(1f),
            onByteItemClicked = {
                onByteItemSelected(if (selectedByteItem != it) it else null)
            },
        )
    }
}
