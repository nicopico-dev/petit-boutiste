package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.extensions.size
import fr.nicopico.petitboutiste.ui.infra.preview.ByteItemsParameterProvider
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@Composable
fun HexDisplay(
    byteItems: List<ByteItem>,
    modifier: Modifier = Modifier,
    selectedByteItem: ByteItem? = null,
    onByteItemClicked: (ByteItem) -> Unit = {},
) {
    if (byteItems.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.FixedSize(40.dp),
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Top
        ) {
            var itemIndex = 0
            items(
                items = byteItems,
                span = { byteItem ->
                    when (byteItem) {
                        is ByteItem.Group -> GridItemSpan(byteItem.size)
                        is ByteItem.Single -> GridItemSpan(1)
                    }
                }
            ) { item ->
                when (item) {
                    is ByteItem.Single -> {
                        // Display a single byte
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .clickable { onByteItemClicked(item) }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
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
                                .padding(horizontal = 4.dp)
                                .clickable { onByteItemClicked(item) }
                                .border(1.dp, Color.Blue)
                                .let {
                                    if (item == selectedByteItem) {
                                        it.background(MaterialTheme.colorScheme.primaryContainer)
                                    } else it
                                }
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

                            val index = if (item.size > 1) {
                                "$itemIndex..${itemIndex + item.bytes.size - 1}"
                            } else itemIndex.toString()
                            Text(
                                text = index,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            )

                            Text(
                                text = item.name ?: "",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    color = Color.Blue
                                )
                            )
                        }
                        itemIndex += item.bytes.size
                    }
                }
            }
        }
    } else Box(modifier)
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
