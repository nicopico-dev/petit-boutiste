package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition

@Composable
fun ByteGroupDefinitionItem(
    definition: ByteGroupDefinition,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onDelete: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(definition.name ?: "[UNNAMED]") },
        supportingContent = {
            with(definition.indexes) {
                Text(
                    "$start..$endInclusive (${count()} bytes)",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        trailingContent = {
            IconButton(
                content = {
                    Icon(Icons.Outlined.Delete, "Remove byte group")
                },
                onClick = onDelete,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
        ),
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp)
            )
    )
}
