/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data.input.base64

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.data.Base64String
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.ui.components.data.input.DataInput

@Composable
fun Base64Input(
    value: DataString,
    onValueChange: (DataString) -> Unit,
    modifier: Modifier = Modifier,
) {

    val base64Value = when (value) {
        is Base64String -> value
        is HexString -> Base64String.fromHexString(value)
        else -> {
            val hexString = HexString(value.hexString)
            Base64String.fromHexString(hexString)
        }
    }

    DataInput(
        value = base64Value,
        adapter = Base64InputAdapter,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}
