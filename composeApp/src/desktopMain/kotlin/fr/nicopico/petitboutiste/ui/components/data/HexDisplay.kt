/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import fr.nicopico.petitboutiste.models.definition.contains
import fr.nicopico.petitboutiste.models.definition.name
import fr.nicopico.petitboutiste.models.definition.size
import fr.nicopico.petitboutiste.models.definition.toByteGroup
import fr.nicopico.petitboutiste.ui.components.foundation.modifier.clickableWithIndication
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.ui.theme.styles
import fr.nicopico.petitboutiste.ui.theme.typography
import fr.nicopico.petitboutiste.utils.compose.Slot
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
    onAddDefinition: (IntRange) -> Unit = {},
) {
    if (byteItems.isNotEmpty()) {
        val isTemporarySelection = remember(byteItems, selectedByteItem) {
            selectedByteItem != null && selectedByteItem !in byteItems
        }

        //region Theme hoisting
        val theme = AppTheme.current

        val dataStyle = theme.typography.data
        val accentColor = theme.colors.accentColor
        val errorColor = theme.colors.errorColor
        val accentContainerColor = theme.colors.accentContainer
        val scrollbarStyle = theme.styles.scrollbarStyle
        //endregion

        val itemIndexStyle = remember {
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }

        val itemNameStyle = remember(accentColor) {
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                color = accentColor,
            )
        }

        BoxWithConstraints(modifier) {
            val availableWidthPx = constraints.maxWidth
            val columnWidthPx = with(LocalDensity.current) {
                COLUMN_WIDTH.toPx().toInt()
            }
            val maxColumnsPerRow = (availableWidthPx / columnWidthPx)

            // Add grid state to track scrolling
            val gridState = rememberLazyGridState()

            var dragAnchorIndex by remember { mutableStateOf<Int?>(null) }

            fun itemIndexAt(offset: Offset): Int? {
                return gridState.layoutInfo.visibleItemsInfo
                    .firstOrNull { itemInfo ->
                        offset.x >= itemInfo.offset.x &&
                            offset.x < itemInfo.offset.x + itemInfo.size.width &&
                            offset.y >= itemInfo.offset.y &&
                            offset.y < itemInfo.offset.y + itemInfo.size.height
                    }
                    ?.index
            }

            fun updateDragSelection(targetIndex: Int?) {
                val anchorIndex = dragAnchorIndex
                if (anchorIndex == null || targetIndex == null) return

                val range = if (anchorIndex <= targetIndex) {
                    anchorIndex..targetIndex
                } else {
                    targetIndex..anchorIndex
                }

                val selectedItems = byteItems.slice(range)

                selectedItems.toByteGroup()
                    ?.let { tempByteGroup ->
                        onByteItemClicked(tempByteGroup)
                    }
            }

            VerticallyScrollableContainer(
                scrollState = gridState as ScrollableState,
                style = scrollbarStyle,
            ) {
                LazyVerticalGrid(
                    columns = GridCells.FixedSize(COLUMN_WIDTH),
                    state = gridState,
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(byteItems) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val index = itemIndexAt(offset)
                                    if (index != null && byteItems[index] is SingleByte) {
                                        dragAnchorIndex = index
                                        updateDragSelection(index)
                                    } else {
                                        dragAnchorIndex = null
                                    }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    updateDragSelection(itemIndexAt(change.position))
                                },
                                onDragEnd = {
                                    dragAnchorIndex = null
                                },
                                onDragCancel = {
                                    dragAnchorIndex = null
                                },
                            )
                        }
                ) {
                    items(
                        items = byteItems,
                        key = { "${it::class.simpleName}-${it.startIndex}" },
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
                        val inSelection = remember(selectedByteItem, item) {
                            selectedByteItem?.contains(item) ?: false
                        }

                        val itemModifier = createItemModifier(
                            item = item,
                            inSelection = inSelection,
                            accentColor = accentColor,
                            accentContainerColor = accentContainerColor,
                            errorColor = errorColor,
                            onByteItemClicked = onByteItemClicked,
                        )

                        val itemView = @Composable {
                            ByteItemView(
                                item = item,
                                dataStyle = dataStyle,
                                indexStyle = itemIndexStyle,
                                nameStyle = itemNameStyle,
                                modifier = itemModifier,
                            )
                        }

                        if (selectedByteItem != null && inSelection
                            && (isTemporarySelection || selectedByteItem.size == 1)) {
                            TemporaryByteGroupContextMenu(
                                selectedByteItem = selectedByteItem,
                                onAddDefinition = onAddDefinition,
                                content = itemView,
                            )
                        } else itemView()
                    }
                }
            }
        }
    } else Box(modifier)
}

private fun createItemModifier(
    item: ByteItem,
    inSelection: Boolean,
    accentColor: Color,
    accentContainerColor: Color,
    errorColor: Color,
    onByteItemClicked: (ByteItem) -> Unit,
) = Modifier
    .padding(4.dp)
    .clickableWithIndication {
        onByteItemClicked(item)
    }
    .drawBehind {
        if (inSelection) {
            drawRect(color = accentContainerColor)
        }

        val borderColor = when (item) {
            is ByteGroup if item.incomplete -> errorColor
            is ByteGroup -> accentColor
            else -> null
        }

        borderColor?.let {
            drawRect(
                color = it,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
    .padding(4.dp)

@Composable
private fun ByteItemView(
    item: ByteItem,
    dataStyle: TextStyle,
    indexStyle: TextStyle,
    nameStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Text(
            text = item.asString(),
            style = dataStyle,
        )

        val index = if (item.startIndex != item.endIndex) {
            "${item.startIndex}..${item.endIndex}"
        } else item.startIndex.toString()
        Text(
            text = index,
            style = indexStyle,
        )

        item.name?.let { name ->
            Text(
                text = name,
                style = nameStyle,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TemporaryByteGroupContextMenu(
    selectedByteItem: ByteItem,
    onAddDefinition: (IntRange) -> Unit,
    content: Slot,
) {
    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Create a new definition") {
                    onAddDefinition(
                        selectedByteItem.startIndex..selectedByteItem.endIndex
                    )
                }
            )
        },
        content = content,
    )
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
