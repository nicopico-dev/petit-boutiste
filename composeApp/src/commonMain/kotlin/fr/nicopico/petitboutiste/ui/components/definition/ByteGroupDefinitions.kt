/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.LocalOnAppEvent
import fr.nicopico.petitboutiste.LocalOnSnackbar
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.createDefinitionId
import fr.nicopico.petitboutiste.models.definition.toJsonData
import fr.nicopico.petitboutiste.state.AppEvent
import fr.nicopico.petitboutiste.state.SnackbarState
import fr.nicopico.petitboutiste.ui.components.foundation.modifier.clickableWithIndication
import fr.nicopico.petitboutiste.utils.incrementIndexSuffix
import fr.nicopico.petitboutiste.utils.moveStart
import fr.nicopico.petitboutiste.utils.setData
import fr.nicopico.petitboutiste.utils.size
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.typography

@Suppress("LongMethod")
@OptIn(ExperimentalComposeUiApi::class)
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
    val overlappingDefinitions: Set<ByteGroupDefinition> = remember(definitions) {
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

    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    val lazyListState = rememberLazyListState()
    val onEvent = LocalOnAppEvent.current
    val onSnackbar = LocalOnSnackbar.current

    Column(modifier) {
        Row(
            Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Definitions",
                style = JewelTheme.typography.h4TextStyle,
                modifier = Modifier.weight(1f),
            )

            IconActionButton(
                key = AllIconsKeys.FileTypes.Json,
                contentDescription = "Export payloads data as JSON",
                enabled = definitions.isNotEmpty() && byteItems.isNotEmpty(),
                onClick = {
                    scope.launch {
                        val json = byteItems.toJsonData()
                        if (clipboard.setData(json)) {
                            onSnackbar(SnackbarState("Data exported to clipboard as JSON"))
                        } else {
                            onSnackbar(SnackbarState("Failed to export data to the clipboard"))
                        }
                    }
                },
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState,
        ) {
            items(definitions) { definition ->
                val byteGroup = byteItems.firstOrNull {
                    it is ByteGroup && it.definition == definition
                } as? ByteGroup

                val expectedSize = definition.indexes.size
                val actualSize = byteGroup?.bytes?.size
                val errorMessage = when {
                    definition in overlappingDefinitions -> {
                        "This definition overlaps with the previous one"
                    }
                    actualSize != null && actualSize != expectedSize -> {
                        "The payload is incomplete ($actualSize bytes instead of $expectedSize)"
                    }
                    else -> null
                }

                ContextMenuArea(
                    items = {
                        listOf(
                            ContextMenuItem("Duplicate this definition") {
                                val event = AppEvent.CurrentTabEvent.AddDefinitionEvent(
                                    definition = definition.copy(
                                        id = createDefinitionId(),
                                        name = definition.name?.incrementIndexSuffix(),
                                        indexes = with(definition.indexes) {
                                            moveStart(endInclusive + 1)
                                        },
                                    )
                                )
                                onEvent(event)
                            }
                        )
                    }
                ) {
                    ByteGroupDefinitionItem(
                        definition = definition,
                        byteGroup = byteGroup,
                        selected = definition == selectedDefinition,
                        modifier = Modifier.clickableWithIndication {
                            if (definition != selectedDefinition) {
                                onDefinitionSelected(definition)
                            } else {
                                onDefinitionSelected(null)
                            }
                        },
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
                        displayForm = openedDefinition == definition,
                        onToggleDisplayForm = { display ->
                            openedDefinition = if (display) definition else null
                        },
                        onDelete = {
                            onDeleteDefinition(definition)
                        },
                        errorMessage = errorMessage,
                    )
                }
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

    // Auto-scroll to opened definition
    LaunchedEffect(openedDefinition) {
        if (openedDefinition == null) return@LaunchedEffect
        val index = definitions
            .indexOfFirst { openedDefinition == it }

        if (index != -1) {
            lazyListState.animateScrollToItem(index)
        }
    }
}
