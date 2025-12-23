/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data.input.binary

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.data.Base64String
import fr.nicopico.petitboutiste.models.data.BinaryString
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.ui.components.data.input.DataInput
import fr.nicopico.petitboutiste.utils.compose.preview.BinaryStringParameterProvider
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop

@Composable
fun BinaryInput(
    value: DataString,
    onValueChange: (DataString) -> Unit,
    modifier: Modifier = Modifier,
) {
    val binaryValue = when (value) {
        is BinaryString -> value
        is HexString -> BinaryString.fromHexString(value)
        is Base64String -> BinaryString.fromHexString(HexString(value.hexString))
    }

    DataInput(
        value = binaryValue,
        adapter = BinaryInputAdapter,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun BinaryInputPreview() {
    WrapForPreviewDesktop(BinaryStringParameterProvider()) {
        BinaryInput(it, onValueChange = {})
    }
}
