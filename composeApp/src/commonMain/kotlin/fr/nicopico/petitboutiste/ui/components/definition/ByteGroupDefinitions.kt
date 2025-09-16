package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.extensions.removeAt
import fr.nicopico.petitboutiste.models.extensions.replace
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography

@Composable
fun ByteGroupDefinitions(
    definitions: List<ByteGroupDefinition>,
    onDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
    modifier: Modifier = Modifier,
    selectedDefinition: ByteGroupDefinition? = null,
    onDefinitionSelected: (ByteGroupDefinition?) -> Unit = {},
    byteItems: List<ByteItem> = emptyList(),
) {
    Column(modifier) {
        Text(
            "Definitions",
            style = JewelTheme.typography.h4TextStyle,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        LazyColumn(
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
