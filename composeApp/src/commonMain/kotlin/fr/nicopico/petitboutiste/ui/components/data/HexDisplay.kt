/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.definition.name
import fr.nicopico.petitboutiste.models.definition.size
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.ui.theme.styles
import fr.nicopico.petitboutiste.ui.theme.typography
import fr.nicopico.petitboutiste.utils.compose.preview.ByteItemsParameterProvider
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticallyScrollableContainer

private val COLUMN_WIDTH = 40.dp

@Composable
fun HexDisplay(
    byteItems: List<ByteItem>,
    modifier: Modifier = Modifier,
    selectedByteItem: ByteItem? = null,
    onByteItemClicked: (ByteItem) -> Unit = {},
) {
    if (byteItems.isNotEmpty()) {
        BoxWithConstraints(modifier) {
            val availableWidthPx = constraints.maxWidth
            val columnWidthPx = with(LocalDensity.current) {
                COLUMN_WIDTH.toPx().toInt()
            }
            val maxColumnsPerRow = (availableWidthPx / columnWidthPx)

            // Add grid state to track scrolling
            val gridState = rememberLazyGridState()

            VerticallyScrollableContainer(
                scrollState = gridState as ScrollableState,
                style = AppTheme.current.styles.scrollbarStyle,
            ) {
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(COLUMN_WIDTH),
                    state = gridState,
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = byteItems,
                        key = { it.firstIndex..it.lastIndex },
                        contentType = { it::class },
                        span = { byteItem ->
                            when (byteItem) {
                                is ByteGroup -> {
                                    // Limit the span to the maximum number of columns per row
                                    val span = minOf(byteItem.size, maxColumnsPerRow)
                                    GridItemSpan(span)
                                }

                                is SingleByte -> GridItemSpan(1)
                            }
                        },
                    ) { item ->
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable { onByteItemClicked(item) }
                                .let {
                                    when (item) {
                                        is SingleByte -> it
                                        is ByteGroup if item.incomplete -> it.border(
                                            1.dp,
                                            AppTheme.current.colors.errorColor
                                        )

                                        is ByteGroup -> it.border(
                                            1.dp,
                                            AppTheme.current.colors.accentColor
                                        )
                                    }
                                }
                                .let {
                                    if (item == selectedByteItem) {
                                        it.background(AppTheme.current.colors.accentContainer)
                                    } else it
                                }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = item.toString(),
                                style = AppTheme.current.typography.data,
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
                                    color = AppTheme.current.colors.accentColor,
                                ),
                                softWrap = false,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    } else Box(modifier)
}

@Preview
@Composable
private fun HexDisplayPreview() {
    WrapForPreviewDesktop(ByteItemsParameterProvider()) { byteItems ->
        HexDisplay(
            byteItems = byteItems,
            modifier = Modifier.height(50.dp),
        )
    }
}
