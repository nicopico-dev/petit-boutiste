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
import fr.nicopico.petitboutiste.models.BinaryString
import fr.nicopico.petitboutiste.models.DataString
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.extensions.formatForDisplay
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@Composable
fun BinaryInput(
    value: DataString,
    onValueChange: (DataString) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Convert DataString to BinaryString if needed
    val binaryValue = when (value) {
        is BinaryString -> value
        is HexString -> BinaryString.fromHexString(value)
        else -> BinaryString("00000000") // Default empty binary string
    }

    var input by remember(binaryValue) {
        mutableStateOf(binaryValue.binaryString)
    }
    var formattedInput by remember(input) {
        mutableStateOf(if (input.isNotEmpty()) {
            try {
                BinaryString(input).formatForDisplay()
            } catch (e: IllegalArgumentException) {
                input
            }
        } else "")
    }
    var isError: Boolean by remember(binaryValue) {
        mutableStateOf(false)
    }

    BasicTextField(
        value = formattedInput.ifEmpty { input },
        onValueChange = {
            // Remove formatting (spaces) for processing
            val rawInput = it.replace(" ", "")
            isError = false
            input = rawInput

            // Only update if the input is valid
            if (rawInput.all { char -> char == '0' || char == '1' }) {
                // Only update the data if the length is a multiple of 8
                if (rawInput.length % 8 == 0 && rawInput.isNotEmpty()) {
                    val dataString = BinaryString.parse(rawInput)
                    if (dataString != null) {
                        onValueChange(dataString)
                        formattedInput = dataString.formatForDisplay()
                    } else {
                        isError = true
                    }
                } else {
                    // Still update the formatted display for partial input
                    formattedInput = rawInput
                }
            } else {
                isError = true
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
                        text = "Paste binary string here (e.g., 01001000 )",
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
private fun BinaryInputPreview() {
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BinaryInput(BinaryString("0101010101010101"), onValueChange = {})
            BinaryInput(BinaryString(""), onValueChange = {})
        }
    }
}
