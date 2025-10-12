package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.TextField

@Deprecated("Use PBLabel to define a label")
@Composable
fun PBTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelOrientation: PBLabelOrientation = PBLabelOrientation.Vertical,
    isError: Boolean = false,
    maxFieldWidth: Dp = Dp.Unspecified,
) {
    PBLabel(label, modifier, labelOrientation) {
        PBTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.widthIn(max = maxFieldWidth).fillMaxWidth(),
            isError = isError,
        )
    }
}

@Composable
fun PBTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    val state = remember(value) {
        TextFieldState(value)
    }

    // Observe changes to the text
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .drop(1)
            .distinctUntilChanged()
            .collect {
                onValueChange(it)
            }
    }

    TextField(
        state = state,
        modifier = modifier,
        outline = if (isError) Outline.Error else Outline.None,
    )
}
