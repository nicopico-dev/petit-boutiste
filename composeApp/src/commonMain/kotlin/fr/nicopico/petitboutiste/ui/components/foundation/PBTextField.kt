package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.typography

@Composable
fun PBTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelPosition: PBLabelPosition = PBLabelPosition.Top,
    isError: Boolean = false,
    maxFieldWidth: Dp = Dp.Unspecified,
) {
    val state = remember(value) {
        TextFieldState(value)
    }

    // Observe changes to the text
    LaunchedEffect(value) {
        snapshotFlow { state.text.toString() }
            .drop(1)
            .distinctUntilChanged()
            .collect {
                onValueChange(it)
            }
    }

    if (labelPosition == PBLabelPosition.Top) {
        Column(modifier) {
            Text(label, style = JewelTheme.typography.medium)
            Spacer(Modifier.size(4.dp))
            TextField(
                state = state,
                modifier = Modifier.widthIn(max = maxFieldWidth).fillMaxWidth(),
                outline = if (isError) Outline.Error else Outline.None,
            )
        }
    } else {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            Text(
                text = label,
                style = JewelTheme.typography.medium,
            )
            Spacer(Modifier.widthIn(min = 4.dp))
            TextField(
                state = state,
                modifier = Modifier.widthIn(max = maxFieldWidth).fillMaxWidth(),
                outline = if (isError) Outline.Error else Outline.None,
            )
        }
    }
}
