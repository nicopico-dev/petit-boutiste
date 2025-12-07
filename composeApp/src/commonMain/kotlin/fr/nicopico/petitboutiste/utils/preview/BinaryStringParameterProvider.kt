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
