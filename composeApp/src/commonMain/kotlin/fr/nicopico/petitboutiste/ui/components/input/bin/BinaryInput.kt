package fr.nicopico.petitboutiste.ui.components.input.bin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.input.Base64String
import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.components.input.DataInput
import fr.nicopico.petitboutiste.utils.preview.WrapForPreview

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
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BinaryInput(BinaryString("0101010101010101"), onValueChange = {})
            BinaryInput(BinaryString(""), onValueChange = {})
        }
    }
}
