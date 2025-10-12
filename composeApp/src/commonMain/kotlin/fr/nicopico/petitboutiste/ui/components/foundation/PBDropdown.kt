package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.ListComboBox
import org.jetbrains.jewel.ui.component.SimpleListItem
import kotlin.math.max

@OptIn(ExperimentalJewelApi::class)
@Composable
fun <T : Any> PBDropdown(
    items: List<T>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    selection: T? = null,
    getItemLabel: (T) -> String = { it.toString() },
) {
    val selectedIndex: Int = remember(items, selection) {
        max(0, items.indexOfFirst { it == selection })
    }

    ListComboBox(
        items = items,
        itemKeys = { _, item: T -> getItemLabel(item) },
        selectedIndex = selectedIndex,
        onSelectedItemChange = { index ->
            onItemSelected(items[index])
        },
        itemContent = { item, isSelected, isActive ->
            SimpleListItem(
                text = getItemLabel(item),
                selected = isSelected,
                active = isActive,
            )
        },
        modifier = modifier,
    )
}
