package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextArea
import org.jetbrains.jewel.ui.typography

@OptIn(ExperimentalJewelApi::class)
@Composable
fun PBTextArea(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
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

    Column(modifier) {
        Text(label, style = JewelTheme.typography.medium)
        Spacer(Modifier.size(4.dp))
        TextArea(
            state = state,
            modifier = Modifier
                .widthIn(max = maxFieldWidth)
                .fillMaxWidth()
                .fillMaxHeight(),
            outline = if (isError) Outline.Error else Outline.None,
            decorationBoxModifier = Modifier.padding(4.dp)
        )
    }
}
