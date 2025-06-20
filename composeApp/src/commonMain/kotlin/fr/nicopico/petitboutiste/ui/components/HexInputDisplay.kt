package fr.nicopico.petitboutiste.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.ByteGroup
import fr.nicopico.petitboutiste.models.OldByteItem
import fr.nicopico.petitboutiste.models.SingleByte
import kotlin.collections.addAll
import kotlin.collections.plus

@Deprecated("")
@Composable
fun HexInputDisplay() {
    var hexInput by rememberSaveable { mutableStateOf("") }
    val formattedHex = remember(hexInput) {
        // Remove any non-hex characters and convert to uppercase
        val cleanHex = hexInput.filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }.uppercase()
        cleanHex
    }

    // Determine if we need to pad the last byte (if the hex string has an odd length)
    val paddedHex = remember(formattedHex) {
        if (formattedHex.length % 2 != 0) {
            "$formattedHex "  // Add a space for the incomplete byte
        } else {
            formattedHex
        }
    }

    // State for byte groups
    var byteGroups by remember { mutableStateOf(listOf<ByteGroup>()) }
    var startIndex by remember { mutableStateOf("") }
    var endIndex by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }

    // Process hex input into a list of ByteItems (single bytes and groups)
    val byteItems = remember(paddedHex, byteGroups) {
        val allBytes = paddedHex.chunked(2)
        val items = mutableListOf<OldByteItem>()

        // Track which indices are part of groups
        val groupedIndices = mutableSetOf<Int>()

        // Add all byte groups
        byteGroups.forEach { group ->
            groupedIndices.addAll(group.startIndex..group.endIndex)
            items.add(group)
        }

        // Add all individual bytes that are not part of any group
        allBytes.forEachIndexed { index, byte ->
            if (index !in groupedIndices) {
                items.add(SingleByte(index, byte))
            }
        }

        // Sort items by their start index
        items.sortedBy {
            when (it) {
                is SingleByte -> it.index
                is ByteGroup -> it.startIndex
                else -> error("not supported")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hex Input",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BasicTextField(
            value = hexInput,
            onValueChange = { hexInput = it },
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, Color.Gray)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (hexInput.isEmpty()) {
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

        // Group creation controls
        if (formattedHex.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = startIndex,
                    onValueChange = { startIndex = it },
                    label = { Text("Start Index") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = endIndex,
                    onValueChange = { endIndex = it },
                    label = { Text("End Index") },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val start = startIndex.toIntOrNull() ?: return@Button
                        val end = endIndex.toIntOrNull() ?: return@Button

                        if (start < 0 || end >= paddedHex.length / 2 || start > end) {
                            return@Button
                        }

                        val bytes = paddedHex.chunked(2).subList(start, end + 1)
                        val newGroup = ByteGroup(start, end, bytes, groupName)

                        // Check if this group overlaps with any existing group
                        val overlaps = byteGroups.any { group ->
                            (start <= group.endIndex && end >= group.startIndex)
                        }

                        if (!overlaps) {
                            byteGroups = byteGroups + newGroup
                        }

                        // Reset input fields
                        startIndex = ""
                        endIndex = ""
                        groupName = ""
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Create Group")
                }
            }
        }

        if (formattedHex.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Top
            ) {
                items(byteItems) { item ->
                    when (item) {
                        is SingleByte -> {
                            // Display a single byte
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.value,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = item.index.toString(),
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                )
                            }
                        }

                        is ByteGroup -> {
                            // Display a byte group
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .border(1.dp, Color.Blue)
                                    .padding(4.dp)
                            ) {
                                // Get the raw hex string for this group without spaces
                                val groupText = item.bytes.joinToString("")

                                Text(
                                    text = groupText,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 18.sp
                                    )
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${item.startIndex}..${item.endIndex}",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    )

                                    // Add a remove button
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .border(1.dp, Color.Red)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                            .clickable {
                                                byteGroups = byteGroups.filter { g -> g != item }
                                            }
                                    ) {
                                        Text(
                                            text = "✕",
                                            style = TextStyle(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp,
                                                color = Color.Red
                                            )
                                        )
                                    }
                                }
                                if (item.name.isNotEmpty()) {
                                    Text(
                                        text = item.name,
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = Color.Blue
                                        )
                                    )
                                }
                            }
                        }

                        else -> error("Not supported")
                    }
                }
            }
        }
    }
}
