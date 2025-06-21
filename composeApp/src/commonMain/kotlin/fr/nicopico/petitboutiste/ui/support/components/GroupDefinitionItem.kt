package fr.nicopico.petitboutiste.ui.support.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupDefinitionItem(
    definition: ByteGroupDefinition,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onDelete: () -> Unit,
) {
    ListItem(
        text = { Text(definition.name ?: "[UNNAMED]") },
        secondaryText = {
            with(definition.indexes) {
                Text(
                    "$start..$endInclusive",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        trailing = {
            IconButton(
                content = {
                    Icon(Icons.Outlined.Delete, "Remove byte group")
                },
                onClick = onDelete,
            )
        },
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp)
            )
            .let {
                if (selected) {
                    it.background(MaterialTheme.colorScheme.primaryContainer)
                } else it
            }

    )
}
