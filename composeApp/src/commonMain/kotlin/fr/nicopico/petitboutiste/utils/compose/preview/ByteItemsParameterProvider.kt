/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.SingleByte

@Suppress("DEPRECATION")
class ByteItemsParameterProvider(
    override val values: Sequence<List<ByteItem>> = sequenceOf(
        listOf(),
        listOf(
            SingleByte(0, "62"),
            SingleByte(1, "6F"),
            SingleByte(2, "6E"),
            SingleByte(3, "6A"),
            SingleByte(4, "6F"),
            SingleByte(5, "75"),
            SingleByte(6, "72"),
        ),
        listOf(
            SingleByte(0, "33"),
            ByteGroup(1, "DAADDAAD"),
            SingleByte(2, "01"),
            SingleByte(3, "15"),
            SingleByte(4, "01"),
            SingleByte(5, "04"),
            SingleByte(6, "00"),
            SingleByte(7, "01"),
            SingleByte(8, "00"),
            SingleByte(9, "00"),
            SingleByte(10, "04"),
        ),
        // Test case with a large ByteGroup that exceeds the default maxColumnsPerRow (8)
        listOf(
            SingleByte(0, "FF"),
            ByteGroup(1, "AABBCCDDEEFF00112233445566778899", "Large ByteGroup"),
            SingleByte(2, "EE"),
        ),
    )
) : PreviewParameterProvider<List<ByteItem>>
