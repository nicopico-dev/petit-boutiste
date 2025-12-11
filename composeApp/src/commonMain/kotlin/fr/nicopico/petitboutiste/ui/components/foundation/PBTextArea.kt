package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.TextArea

@OptIn(ExperimentalJewelApi::class)
@Composable
fun PBTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    textStyle: TextStyle = JewelTheme.defaultTextStyle,
) {
    val state = observeTextFieldState(value, onValueChange)

    TextArea(
        state = state,
        modifier = modifier,
        outline = if (isError) Outline.Error else Outline.None,
        decorationBoxModifier = Modifier.padding(4.dp),
        textStyle = textStyle,
    )
}
