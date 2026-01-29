/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data.input.hexadecimal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.nicopico.petitboutiste.models.data.Base64String
import fr.nicopico.petitboutiste.models.data.BinaryString
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.ui.components.data.input.DataInput
import fr.nicopico.petitboutiste.utils.compose.preview.HexStringParameterProvider
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop

@Composable
fun HexInput(
    value: DataString,
    onValueChange: (DataString) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hexValue = remember(value) {
        when (value) {
            is HexString -> value
            is BinaryString -> HexString(value.hexString)
            is Base64String -> HexString(value.hexString)
        }
    }

    DataInput(
        value = hexValue,
        adapter = HexInputAdapter,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun HexInputPreview() {
    WrapForPreviewDesktop(HexStringParameterProvider()) {
        HexInput(it, onValueChange = {})
    }
}
