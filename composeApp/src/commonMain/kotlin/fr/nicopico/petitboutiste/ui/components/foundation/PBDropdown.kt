package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.ListComboBox
import org.jetbrains.jewel.ui.component.SimpleListItem
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography
import kotlin.math.max

@OptIn(ExperimentalJewelApi::class)
@Composable
fun <T : Any> PBDropdown(
    items: List<T>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    selection: T? = null,
    label: String? = null,
    getItemLabel: (T) -> String = { it.toString() },
) {
    val selectedIndex: Int = remember(items, selection) {
        max(0, items.indexOfFirst { it == selection })
    }

    Column(modifier) {
        if (label != null) {
            Text(label, style = JewelTheme.typography.medium)
            Spacer(Modifier.height(4.dp))
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
        )
    }
}
