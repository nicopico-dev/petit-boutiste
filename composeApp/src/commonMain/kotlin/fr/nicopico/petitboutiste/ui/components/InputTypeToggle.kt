package fr.nicopico.petitboutiste.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.InputType

@Composable
fun InputTypeToggle(
    current: InputType,
    onInputTypeChange: (InputType) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Format toggle button
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .border(1.dp, Color.LightGray)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        InputType.entries.forEach { inputType ->
            val selected = inputType == current
            Text(
                text = (if (selected) "→" else " ") + inputType.label,
                fontSize = 10.sp,
                color = if (selected) Color.Blue else Color.Gray,
                modifier = Modifier.clickable(enabled = !selected) {
                    onInputTypeChange(inputType)
                }
            )
        }
    }
}

private val InputType.label: String
    get() = when (this) {
        InputType.HEX -> "HEX"
        InputType.BINARY -> "BIN"
    }
