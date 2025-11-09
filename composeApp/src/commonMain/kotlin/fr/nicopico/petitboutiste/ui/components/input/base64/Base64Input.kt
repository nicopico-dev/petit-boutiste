package fr.nicopico.petitboutiste.ui.components.input.base64

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.input.Base64String
import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.components.input.DataInput

@Composable
fun Base64Input(
    value: DataString,
    onValueChange: (DataString) -> Unit,
    modifier: Modifier = Modifier,
) {

    // TODO Convert binary and hex to base64
    val base64Value = when (value) {
        is Base64String -> value
        is BinaryString -> Base64String()
        is HexString -> Base64String()
    }

    DataInput(
        value = base64Value,
        adapter = Base64InputAdapter,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}
