package fr.nicopico.petitboutiste.ui.infra.preview

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class ByteItemsParameterProvider(
    override val values: Sequence<List<ByteItem>> = sequenceOf(
        listOf(),
        listOf(
            ByteItem.Single("62"),
            ByteItem.Single("6F"),
            ByteItem.Single("6E"),
            ByteItem.Single("6A"),
            ByteItem.Single("6F"),
            ByteItem.Single("75"),
            ByteItem.Single("72"),
        ),
        listOf(
            ByteItem.Single("33"),
            ByteItem.Group(
                listOf(
                    ByteItem.Single("DA"),
                    ByteItem.Single("AD"),
                    ByteItem.Single("DA"),
                    ByteItem.Single("AD"),
                ),
                definition = ByteGroupDefinition(
                    1..4
                ),
            ),
            ByteItem.Single("01"),
            ByteItem.Single("15"),
            ByteItem.Single("01"),
            ByteItem.Single("04"),
            ByteItem.Single("00"),
            ByteItem.Single("01"),
            ByteItem.Single("00"),
            ByteItem.Single("00"),
            ByteItem.Single("04"),
        ),
    )
) : PreviewParameterProvider<List<ByteItem>>
