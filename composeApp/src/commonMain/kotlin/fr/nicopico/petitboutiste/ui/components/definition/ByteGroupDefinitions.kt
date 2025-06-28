package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.extensions.removeAt
import fr.nicopico.petitboutiste.models.extensions.replace

@Composable
fun ByteGroupDefinitions(
    definitions: List<ByteGroupDefinition>,
    onDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
    modifier: Modifier = Modifier,
    selectedDefinition: ByteGroupDefinition? = null,
    onDefinitionSelected: (ByteGroupDefinition?) -> Unit = {},
    byteItems: List<ByteItem> = emptyList(),
) {
    var collapsed by remember {
        mutableStateOf(false)
    }

    Column(modifier) {
        Row(
            Modifier
                .clickable {
                    collapsed = !collapsed
                }
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Definitions",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                if (collapsed) Icons.Default.UnfoldMore else Icons.Default.UnfoldLess,
                "Toggle",
                modifier = Modifier.size(18.dp)
            )
        }

        if (!collapsed) {
            LazyColumn(
                Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(definitions) { index, definition ->
                    val byteGroup = byteItems.firstOrNull {
                        it is ByteItem.Group && it.definition == definition
                    } as? ByteItem.Group

                    ByteGroupDefinitionItem(
                        definition = definition,
                        byteGroup = byteGroup,
                        selected = definition == selectedDefinition,
                        modifier = Modifier.clickable {
                            if (definition != selectedDefinition) {
                                onDefinitionSelected(definition)
                            } else {
                                onDefinitionSelected(null)
                            }
                        },
                        onDelete = {
                            onDefinitionsChanged(definitions.removeAt(index))
                        }
                    )
                }

                item {
                    ByteGroupDefinitionForm(
                        definition = selectedDefinition,
                        onDefinitionSaved = { savedDefinition ->
                            val updatedDefinitions = if (selectedDefinition != null) {
                                definitions.replace(selectedDefinition, savedDefinition)
                            } else {
                                definitions + savedDefinition
                            }

                            onDefinitionsChanged(updatedDefinitions.sortedBy { it.indexes.start })
                        },
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 24.dp),
                    )
                }
            }
        }
    }
}
