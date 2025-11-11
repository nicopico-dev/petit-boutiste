package fr.nicopico.petitboutiste.ui.components.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.ui.components.foundation.PBTextArea
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.typography

/**
 * Generic, factored input field for data-like strings.
 */
@Composable
fun <T : DataString> DataInput(
    value: T,
    adapter: DataInputAdapter<T>,
    onValueChange: (DataString) -> Unit,
    modifier: Modifier = Modifier,
) {
    var input by remember(value) {
        mutableStateOf(adapter.getNormalizedString(value))
    }
    var isError by remember(value) {
        mutableStateOf(false)
    }

    PBTextArea(
        value = input,
        onValueChange = { newText ->
            val parsed = adapter.parse(newText)
            input = newText

            isError = (parsed == null)
            if (parsed != null && parsed != value) {
                log("initial value: $value, parsed: $parsed -> onValueChange")
                onValueChange(parsed)
            }
        },
        isError = isError,
        modifier = modifier,
        textStyle = AppTheme.current.typography.data,
    )
}
