/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import fr.nicopico.petitboutiste.models.data.BinaryString
import fr.nicopico.petitboutiste.models.data.DataString

class BinaryStringParameterProvider(
    override val values: Sequence<DataString> = sequenceOf(
        BinaryString(""),
        BinaryString("0101010101010101"),
    )
) : PreviewParameterProvider<DataString> {}
