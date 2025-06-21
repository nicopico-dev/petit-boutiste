package fr.nicopico.petitboutiste.ui.support

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.removeAt
import fr.nicopico.petitboutiste.models.updateGroupDefinitions
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import fr.nicopico.petitboutiste.ui.support.components.ByteGroupControls
import fr.nicopico.petitboutiste.ui.support.components.GroupContent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupManagementPane(
    groupDefinitions: List<ByteGroupDefinition>,
    modifier: Modifier = Modifier,
    selectedGroup: ByteItem.Group? = null,
    onGroupDefinitionSelected: (ByteGroupDefinition?) -> Unit = {},
    onGroupDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit
) {
    var selectedIndex: Int? by remember(groupDefinitions, selectedGroup) {
        val initialValue = if (selectedGroup == null) {
            null
        } else {
            groupDefinitions.indexOfFirst { it == selectedGroup.definition }
        }
        mutableStateOf(initialValue)
    }
    val selectedGroupDefinition by remember(groupDefinitions) {
        derivedStateOf {
            selectedIndex
                ?.let { groupDefinitions[it] }
        }
    }

    Column(modifier) {
        LazyColumn(
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
                            if (selectedIndex == index) {
                                it.background(MaterialTheme.colorScheme.primaryContainer)
                            } else it
                        }
                        .clickable {
                            if (selectedIndex != index) {
                                selectedIndex = index
                                onGroupDefinitionSelected(groupDefinitions[index])
                            } else {
                                selectedIndex = null
                                onGroupDefinitionSelected(null)
                            }
                        }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier.border(1.dp, Color.LightGray).padding(16.dp),
        ) {
            Text(
                text = if (selectedGroup == null) { "Create new group" } else "Update group",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            ByteGroupControls(
                groupDefinition = selectedGroupDefinition,
                onDefinitionChanged = { savedDefinition ->
                    onGroupDefinitionsChanged(
                        groupDefinitions.updateGroupDefinitions(selectedIndex, savedDefinition)
                    )
                },
            )
        }

        Spacer(Modifier.weight(1f))

        if (selectedGroup != null) {
            GroupContent(selectedGroup, Modifier.fillMaxWidth())
        }
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
