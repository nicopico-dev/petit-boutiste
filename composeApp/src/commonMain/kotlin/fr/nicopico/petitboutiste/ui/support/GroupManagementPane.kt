package fr.nicopico.petitboutiste.ui.support

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.removeAt
import fr.nicopico.petitboutiste.models.updateGroupDefinitions
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import fr.nicopico.petitboutiste.ui.support.components.ByteGroupControls

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupManagementPane(
    groupDefinitions: List<ByteGroupDefinition>,
    modifier: Modifier = Modifier,
    onGroupDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
) {
    var selectedGroupIndex: Int? by remember {
        mutableStateOf(null)
    }

    Column(modifier) {
        LazyColumn(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(groupDefinitions) { index, definition ->
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
                            onClick = {
                                onGroupDefinitionsChanged(groupDefinitions.removeAt(index))
                            },
                        )
                    },
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .let {
                            if (selectedGroupIndex == index) {
                                it.background(MaterialTheme.colorScheme.primaryContainer)
                            } else it
                        }
                        .clickable {
                            selectedGroupIndex = if (selectedGroupIndex != index) index else null
                        }
                )
            }
        }

        ByteGroupControls(
            groupDefinition = selectedGroupIndex?.let { groupDefinitions[it] },
            onDefinitionChanged = { savedDefinition ->
                onGroupDefinitionsChanged(
                    groupDefinitions.updateGroupDefinitions(selectedGroupIndex, savedDefinition)
                )
            }
        )
    }
}

@Preview
@Composable
private fun GroupManagementPanePreview() {
    WrapForPreview {
        GroupManagementPane(
            groupDefinitions = listOf(
                ByteGroupDefinition(
                    indexes = 1..4,
                ),
                ByteGroupDefinition(
                    indexes = 5..6,
                    name = "Some group"
                )
            ),
            onGroupDefinitionsChanged = {}
        )
    }
}
