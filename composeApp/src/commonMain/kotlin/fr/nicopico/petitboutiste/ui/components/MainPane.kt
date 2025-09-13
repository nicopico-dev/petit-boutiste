package fr.nicopico.petitboutiste.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.extensions.toByteItems
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.ui.components.input.BinaryInput
import fr.nicopico.petitboutiste.ui.components.input.HexInput
import fr.nicopico.petitboutiste.ui.components.input.InputTypeToggle
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import org.jetbrains.jewel.ui.component.Text

@Composable
fun MainPane(
    inputData: DataString,
    onInputDataChanged: (DataString) -> Unit,
    modifier: Modifier = Modifier.Companion,
    byteItems: List<ByteItem> = inputData.toByteItems(),
    selectedByteItem: ByteItem? = null,
    onByteItemSelected: (ByteItem?) -> Unit = {},
    inputType: InputType = InputType.HEX,
    onInputTypeChanged: (InputType) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = "Data Input",
                style = JewelThemeUtils.typography.title,
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Center)
            )

            InputTypeToggle(
                inputType,
                onInputTypeChanged,
                Modifier.align(Alignment.CenterEnd)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Render the appropriate input component based on the selected input type
        val inputModifier = Modifier
            .heightIn(max = 120.dp)
            .background(JewelThemeUtils.colors.inputBackgroundColor)
        when (inputType) {
            InputType.HEX -> HexInput(
                value = inputData,
                onValueChange = { onInputDataChanged(it) },
                modifier = inputModifier
            )

            InputType.BINARY -> BinaryInput(
                value = inputData,
                onValueChange = { onInputDataChanged(it) },
                modifier = inputModifier
            )
        }

        Spacer(Modifier.height(16.dp))

        HexDisplay(
            byteItems = byteItems,
            selectedByteItem = selectedByteItem,
            modifier = Modifier.weight(1f),
            onByteItemClicked = {
                onByteItemSelected(if (selectedByteItem != it) it else null)
            },
        )
    }
}
