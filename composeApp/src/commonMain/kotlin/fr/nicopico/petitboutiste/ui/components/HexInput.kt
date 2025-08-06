package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.InputFormat
import fr.nicopico.petitboutiste.ui.infra.preview.HexStringParameterProvider
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HexInput(
    value: HexString,
    onValueChange: (HexString) -> Unit,
    modifier: Modifier = Modifier,
) {
    var input by remember(value) {
        mutableStateOf(value.hexString)
    }
    var isError: Boolean by remember(value) {
        mutableStateOf(false)
    }
    var selectedInputFormat by remember {
        mutableStateOf(InputFormat.HEXADECIMAL)
    }

    // Convert the current hex value to binary when switching to binary mode
    var binaryInput by remember(value, selectedInputFormat) {
        mutableStateOf(
            if (selectedInputFormat == InputFormat.BINARY && value.hexString.isNotEmpty()) {
                HexString.hexToBinary(value.hexString)
            } else {
                ""
            }
        )
    }

    // The actual input displayed depends on the current input format
    val displayedInput = if (selectedInputFormat == InputFormat.HEXADECIMAL) input else binaryInput

    // Placeholder text based on input format
    val placeholderText = if (selectedInputFormat == InputFormat.HEXADECIMAL) {
        "Paste hexadecimal string here (e.g., 48656C6C6F)"
    } else {
        "Paste binary string here (e.g., 0101010110101010)"
    }

    Column(modifier = modifier) {
        BasicTextField(
            value = displayedInput,
            onValueChange = {
                isError = false

                if (selectedInputFormat == InputFormat.HEXADECIMAL) {
                    input = it
                    if (input.length % 2 == 0) {
                        val hexString = HexString.parse(input)
                        if (hexString != null) {
                            onValueChange(hexString)
                        } else {
                            isError = true
                        }
                    }
                } else {
                    // Binary input
                    binaryInput = it
                    // Only validate and convert if we have a valid binary string
                    if (binaryInput.all { c -> c == '0' || c == '1' }) {
                        val hexString = HexString.parse(binaryInput, InputFormat.BINARY)
                        if (hexString != null) {
                            onValueChange(hexString)
                            // Update the hex input for when we switch back
                            input = hexString.hexString
                        } else {
                            isError = true
                        }
                    } else if (binaryInput.isNotEmpty()) {
                        isError = true
                    }
                }
            },
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color = if (isError) Color.Red else Color.Gray)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Position the toggle in the top-right corner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        // Format toggle button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .border(1.dp, Color.LightGray)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            InputFormat.entries.forEach { format ->
                                val selected = format == selectedInputFormat
                                Text(
                                    text = (if (selected) "→" else " ") + format.label,
                                    fontSize = 10.sp,
                                    color = if (selected) Color.Blue else Color.Gray,
                                    modifier = Modifier.clickable(enabled = !selected) {
                                        if (format == InputFormat.BINARY) {
                                            // Convert current hex to binary for display
                                            if (input.isNotEmpty()) {
                                                binaryInput = HexString.hexToBinary(input)
                                            }
                                        }
                                        selectedInputFormat = format
                                    }
                                )
                            }
                        }
                    }

                    // Center the text field and placeholder
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 8.dp,
                                end = 16.dp, // Keep space for the InputFormat toggle
                            )
                    ) {
                        if (displayedInput.isEmpty()) {
                            Text(
                                text = placeholderText,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

private val InputFormat.label: String
    get() = when (this) {
        InputFormat.HEXADECIMAL -> "HEX"
        InputFormat.BINARY -> "BIN"
    }

@Preview
@Composable
private fun HexInputPreview() {
    val parameterProvider = remember { HexStringParameterProvider() }
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Hex and Binary Input Preview", fontSize = 16.sp)

            parameterProvider.values.forEach { hexString ->
                HexInput(hexString, onValueChange = {})
            }

            // Empty input to show placeholder
            HexInput(HexString(""), onValueChange = {})
        }
    }
}
