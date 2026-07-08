/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
    onAddDefinition: (IntRange) -> Unit= {},
) {
    val byteCount by remember(inputData) {
        derivedStateOf { inputData.hexStringValue.count() / 2 }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Data Input",
                style = AppTheme.current.typography.title,
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = "($byteCount bytes)",
                style = AppTheme.current.typography.small,
            )

            Spacer(Modifier.weight(1f))

            InputTypeToggle(
                inputData.inputType,
                onInputTypeChanged,
                Modifier.testTag(UiTags.INPUT_TYPE_TOGGLE)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Render the appropriate input component based on the selected input type
        val inputModifier = Modifier
            .heightIn(max = 120.dp)
            .fillMaxWidth()
            .testTag(UiTags.DATA_INPUT)
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
            onAddDefinition = onAddDefinition,
        )
    }
}
