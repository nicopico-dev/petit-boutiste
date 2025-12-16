/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.preview

import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.models.input.DataString
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class BinaryStringParameterProvider(
    override val values: Sequence<DataString> = sequenceOf(
        BinaryString(""),
        BinaryString("0101010101010101"),
    )
) : PreviewParameterProvider<DataString> {}
