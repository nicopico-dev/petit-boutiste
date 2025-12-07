package fr.nicopico.petitboutiste.ui.components.input.hex

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.input.Base64String
import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.components.input.DataInput
import fr.nicopico.petitboutiste.utils.preview.HexStringParameterProvider
import fr.nicopico.petitboutiste.utils.preview.WrapForPreviewDesktop

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
