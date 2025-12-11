package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun PBTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    val state = observeTextFieldState(value, onValueChange)

    TextField(
        state = state,
        modifier = modifier,
        outline = if (isError) Outline.Error else Outline.None,
    )
}
