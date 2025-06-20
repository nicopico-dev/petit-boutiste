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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.ui.preview.ByteItemsParameterProvider
import fr.nicopico.petitboutiste.ui.preview.WrapForPreview

@Composable
fun HexDisplay(
    byteItems: List<ByteItem>,
    modifier: Modifier = Modifier,
    onRemoveGroupClicked: (ByteItem.Group) -> Unit = {},
) {
    var byteGroups by remember(byteItems) {
        mutableStateOf(byteItems.filterIsInstance<ByteItem.Group>())
    }

    if (byteItems.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Top
        ) {
            var itemIndex = 0
            items(byteItems) { item ->
                when (item) {
                    is ByteItem.Single -> {
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
                                text = "$itemIndex",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )
                        }
                        itemIndex += 1
                    }

                    is ByteItem.Group -> {
                        // Display a byte group
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .border(1.dp, Color.Blue)
                                .padding(4.dp)
                        ) {
                            // Get the raw hex string for this group without spaces
                            val groupText = item.toString()

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
                                    text = "$itemIndex..${itemIndex + item.bytes.size - 1}",
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
                                            onRemoveGroupClicked(item)
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
                            if (item.name != null && item.name.isNotBlank()) {
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
                        itemIndex += item.bytes.size
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HexDisplayPreview() {
    val parameterProvider = remember { ByteItemsParameterProvider() }
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            parameterProvider.values.forEach { byteItems ->
                HexDisplay(byteItems)
            }
        }
    }
}
