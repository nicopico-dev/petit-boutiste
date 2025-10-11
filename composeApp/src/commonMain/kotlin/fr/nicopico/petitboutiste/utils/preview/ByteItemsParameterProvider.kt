package fr.nicopico.petitboutiste.utils.preview

import fr.nicopico.petitboutiste.models.ByteItem
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class ByteItemsParameterProvider(
    override val values: Sequence<List<ByteItem>> = sequenceOf(
        listOf(),
        listOf(
            ByteItem.Single(0, "62"),
            ByteItem.Single(1, "6F"),
            ByteItem.Single(2, "6E"),
            ByteItem.Single(3, "6A"),
            ByteItem.Single(4, "6F"),
            ByteItem.Single(5, "75"),
            ByteItem.Single(6, "72"),
        ),
        listOf(
            ByteItem.Single(0, "33"),
            ByteItem.Group(1, "DAADDAAD"),
            ByteItem.Single(2, "01"),
            ByteItem.Single(3, "15"),
            ByteItem.Single(4, "01"),
            ByteItem.Single(5, "04"),
            ByteItem.Single(6, "00"),
            ByteItem.Single(7, "01"),
            ByteItem.Single(8, "00"),
            ByteItem.Single(9, "00"),
            ByteItem.Single(10, "04"),
        ),
        // Test case with a large ByteGroup that exceeds the default maxColumnsPerRow (8)
        listOf(
            ByteItem.Single(0, "FF"),
            ByteItem.Group(1, "AABBCCDDEEFF00112233445566778899", "Large Group"),
            ByteItem.Single(2, "EE"),
        ),
    )
) : PreviewParameterProvider<List<ByteItem>>
