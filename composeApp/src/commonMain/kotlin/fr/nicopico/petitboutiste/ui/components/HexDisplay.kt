package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.extensions.name
import fr.nicopico.petitboutiste.models.extensions.size
import fr.nicopico.petitboutiste.ui.infra.preview.ByteItemsParameterProvider
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

private val COLUMN_WIDTH = 40.dp

@Composable
fun HexDisplay(
    byteItems: List<ByteItem>,
    modifier: Modifier = Modifier,
    selectedByteItem: ByteItem? = null,
    onByteItemClicked: (ByteItem) -> Unit = {},
) {

    if (byteItems.isNotEmpty()) {
        BoxWithConstraints {
            val availableWidthPx = constraints.maxWidth
            val columnWidthPx = with(LocalDensity.current) {
                COLUMN_WIDTH.toPx().toInt()
            }
            val maxColumnsPerRow = (availableWidthPx / columnWidthPx)

            // Add grid state to track scrolling
            val gridState = rememberLazyGridState()

            // Use Box to position the grid and scrollbar
            Box(modifier = modifier) {
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(COLUMN_WIDTH),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(
                        items = byteItems,
                        span = { byteItem ->
                            when (byteItem) {
                                is ByteItem.Group -> {
                                    // Limit the span to the maximum number of columns per row
                                    val span = minOf(byteItem.size, maxColumnsPerRow)
                                    GridItemSpan(span)
                                }

                                is ByteItem.Single -> GridItemSpan(1)
                            }
                        }
                    ) { item ->
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { onByteItemClicked(item) }
                                .let {
                                    if (item is ByteItem.Group) {
                                        it.border(1.dp, Color.Blue)
                                    } else it
                                }
                                .let {
                                    if (item == selectedByteItem) {
                                        it.background(MaterialTheme.colorScheme.primaryContainer)
                                    } else it
                                }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = item.toString(),
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 18.sp
                                )
                            )

                            val index = if (item.firstIndex != item.lastIndex) {
                                "${item.firstIndex}..${item.lastIndex}"
                            } else item.firstIndex.toString()
                            Text(
                                text = index,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
                            )

                            Text(
                                text = item.name ?: "",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 8.sp,
                                    color = Color.Blue
                                ),
                                softWrap = false,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(gridState),
            )
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
                HexDisplay(
                    byteItems = byteItems,
                )
            }
        }
    }
}
