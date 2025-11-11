package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.utils.jewel.updateStateValue
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.TextField

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

    LaunchedEffect(value) {
        state.updateStateValue(value)
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
