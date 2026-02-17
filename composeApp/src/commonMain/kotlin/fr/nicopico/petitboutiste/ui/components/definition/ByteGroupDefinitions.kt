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
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.LocalOnAppEvent
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.createDefinitionId
import fr.nicopico.petitboutiste.models.representation.asString
import fr.nicopico.petitboutiste.models.representation.isOff
import fr.nicopico.petitboutiste.models.representation.isReady
import fr.nicopico.petitboutiste.state.AppEvent
import fr.nicopico.petitboutiste.ui.components.foundation.modifier.clickableWithIndication
import fr.nicopico.petitboutiste.utils.incrementIndexSuffix
import fr.nicopico.petitboutiste.utils.logError
import fr.nicopico.petitboutiste.utils.moveStart
import fr.nicopico.petitboutiste.utils.size
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography
import java.awt.datatransfer.StringSelection

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

    val lazyListState = rememberLazyListState()
    val onEvent = LocalOnAppEvent.current
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

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
                    OutlinedButton(
                        content = { Text("Export payloads") },
                        enabled = definitions.isNotEmpty(),
                        onClick = {
                            scope.launch {
                                val payloadEntries = definitions.map { definition ->
                                    val byteGroup = byteItems.firstOrNull {
                                        it is ByteGroup && it.definition == definition
                                    } as? ByteGroup

                                    val renderedValue = if (
                                        byteGroup != null
                                        && !definition.representation.isOff
                                        && definition.representation.isReady
                                    ) {
                                        byteGroup.getOrComputeRendering().asString()
                                    } else null

                                    (definition.name ?: "[UNNAMED]") to renderedValue
                                }
                                val export = buildJsonLikePayloads(payloadEntries)
                                val clipEntry = ClipEntry(StringSelection(export))
                                try {
                                    clipboard.setClipEntry(clipEntry)
                                } catch (e: Exception) {
                                    ensureActive()
                                    logError("Failed to export payloads to clipboard", e)
                                }
                            }
                        },
                    )

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

private fun buildJsonLikePayloads(entries: List<Pair<String, String?>>): String {
    if (entries.isEmpty()) return "{}"

    val content = entries.joinToString(separator = ",\n") { (name, rendered) ->
        val escapedName = name.escapeJsonLike()
        val renderedValue = rendered?.let { "\"${it.escapeJsonLike()}\"" } ?: "null"
        "  \"$escapedName\": $renderedValue"
    }
    return "{\n$content\n}"
}

private fun String.escapeJsonLike(): String {
    val source = this
    return buildString(source.length) {
        source.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
    }
}
