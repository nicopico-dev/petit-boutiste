package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.typography

@Composable
fun PBTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = remember(value) { TextFieldState(value) }

    // Observe changes to the text
    LaunchedEffect(value) {
        snapshotFlow { state.text.toString() }
            .distinctUntilChanged()
            .drop(1)
            .collectLatest {
                onValueChange(it)
            }
    }

    Column(modifier) {
        Text(label, style = JewelTheme.typography.medium)
        Spacer(Modifier.height(4.dp))
        TextField(
            state = state,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
