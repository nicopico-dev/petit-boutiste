package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.infra.preview.HexStringParameterProvider
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@Composable
fun HexInput(
    value: DataString,
    onValueChange: (DataString) -> Unit,
    modifier: Modifier = Modifier,
) {
    var input by remember(value) {
        mutableStateOf(value.hexString)
    }
    var isError: Boolean by remember(value) {
        mutableStateOf(false)
    }

    BasicTextField(
        value = input,
        onValueChange = {
            isError = false
            input = it
            if (input.length % 2 == 0) {
                val dataString = HexString.parse(input)
                if (dataString != null) {
                    onValueChange(dataString)
                } else {
                    isError = true
                }
            }
        },
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        ),
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
                        text = "Paste hexadecimal string here (e.g., 48656C6C6F)",
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

@Preview
@Composable
private fun HexInputPreview() {
    val parameterProvider = remember { HexStringParameterProvider() }
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            parameterProvider.values.forEach { hexString ->
                HexInput(hexString, onValueChange = {})
            }
        }
    }
}
