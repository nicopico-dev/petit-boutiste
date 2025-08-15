package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.utils.compose.optionalSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Dropdown(
    items: List<T>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    selection: T? = null,
    label: String? = null,
    getItemLabel: (T) -> String = { it.toString() },
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selection?.toString() ?: "-- Choose an option --",
            onValueChange = { /* no-op for read-only display */ },
            readOnly = true,
            label = label?.optionalSlot { Text(it) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(getItemLabel(selectionOption))
                    },
                    onClick = {
                        onItemSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }

    }
}
