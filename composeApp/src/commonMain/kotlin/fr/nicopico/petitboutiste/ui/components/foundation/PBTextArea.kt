package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.TextArea

@OptIn(ExperimentalJewelApi::class)
@Composable
fun PBTextArea(
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

    TextArea(
        state = state,
        modifier = modifier,
        outline = if (isError) Outline.Error else Outline.None,
        decorationBoxModifier = Modifier.padding(4.dp)
    )
}
