package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography

@Composable
fun ByteGroupDefinitions(
    definitions: List<ByteGroupDefinition>,
    onAddDefinition: (ByteGroupDefinition) -> Unit,
    onUpdateDefinition: (source: ByteGroupDefinition, update: ByteGroupDefinition) -> Unit,
    onDeleteDefinition: (ByteGroupDefinition) -> Unit,
    modifier: Modifier = Modifier,
    selectedDefinition: ByteGroupDefinition? = null,
    onDefinitionSelected: (ByteGroupDefinition?) -> Unit = {},
    byteItems: List<ByteItem> = emptyList(),
) {
    val invalidDefinitions: Set<ByteGroupDefinition> = remember(definitions) {
        buildSet {
            var previousDefinitionEnd = -1
            definitions.forEach { definition ->
                if (definition.indexes.first <= previousDefinitionEnd) {
                    add(definition)
                }
                previousDefinitionEnd = definition.indexes.last
            }
        }
    }

    var openedDefinition by remember {
        mutableStateOf<ByteGroupDefinition?>(null)
    }

    // Auto-scroll to opened definition
    val lazyListState = rememberLazyListState()
    LaunchedEffect(openedDefinition) {
        if (openedDefinition == null) return@LaunchedEffect
        val index = byteItems.indexOfFirst {
            openedDefinition == (it as? ByteItem.Group)?.definition
        }
        if (index != -1) {
            launch {
                lazyListState.animateScrollToItem(index)
            }
        }
    }

    Column(modifier) {
        Text(
            "Definitions",
            style = JewelTheme.typography.h4TextStyle,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState,
        ) {
            items(definitions) { definition ->
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
                        onDeleteDefinition(definition)
                    },
                    invalidDefinition = definition in invalidDefinitions,
                    form = {
                        ByteGroupDefinitionForm(
                            definition = definition,
                            onDefinitionSaved = { savedDefinition ->
                                onUpdateDefinition(definition, savedDefinition)
                            },
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp)
                                .align(Alignment.End),
                        )
                    },
                    displayForm = openedDefinition == definition ,
                    onToggleDisplayForm = { display ->
                        openedDefinition = if (display) definition else null
                    },
                )
            }

            item {
                Row(Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(
                        content = { Text("Add definition") },
                        onClick = {
                            val nextIndex: Int = if (definitions.isEmpty()) 0 else definitions.last().indexes.last + 1
                            val definition = ByteGroupDefinition(
                                indexes = nextIndex..nextIndex
                            )
                            // Open the new definition automatically
                            openedDefinition = definition
                            onAddDefinition(definition)
                        },
                    )
                }
            }
        }
    }
}
