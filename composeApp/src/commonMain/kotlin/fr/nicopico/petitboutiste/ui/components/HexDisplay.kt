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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.extensions.name
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
            items(
                items = byteItems,
                span = { byteItem ->
                    when (byteItem) {
                        is ByteItem.Group -> GridItemSpan(byteItem.size)
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
                        ),
                        softWrap = false,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
