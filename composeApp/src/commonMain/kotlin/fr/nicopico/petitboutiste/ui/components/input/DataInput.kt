package fr.nicopico.petitboutiste.ui.components.input

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import org.jetbrains.jewel.ui.component.Text

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

    BasicTextField(
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
        textStyle = JewelThemeUtils.typography.data,
        modifier = modifier
            .border(1.dp, color = if (isError) Color.Red else Color.Gray)
            .padding(8.dp),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (input.isEmpty()) {
                    Text(
                        text = adapter.placeholder,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                innerTextField()
            }
        }
    )
}
