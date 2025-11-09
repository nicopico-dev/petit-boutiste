package fr.nicopico.petitboutiste.ui.components.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
        mutableStateOf(adapter.toText(value))
    }
    var isError by remember(value) {
        mutableStateOf(false)
    }

    PBTextArea(
        value = adapter.formatForDisplay(input),
        onValueChange = { newText ->
            val sanitized = adapter.sanitize(newText)
            isError = false
            input = sanitized

            if (adapter.isValid(sanitized)) {
                if (adapter.isReady(sanitized)) {
                    val parsed = adapter.parse(sanitized)
                    if (parsed != null) {
                        onValueChange(parsed)
                        input = adapter.toText(parsed)
                    } else isError = true
                }
            } else isError = true
        },
        isError = isError,
        modifier = modifier,
        textStyle = AppTheme.current.typography.data,
    )
}
