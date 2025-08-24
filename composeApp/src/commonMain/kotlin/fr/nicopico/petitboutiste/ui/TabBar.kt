package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId

/**
 * A tab bar that displays tabs and allows switching between them
 */
@Composable
fun TabBar(
    tabs: List<TabData>,
    selectedTabId: TabId,
    onTabSelected: (TabId) -> Unit,
    onTabAdded: () -> Unit,
    onTabClosed: (TabId) -> Unit,
    onTabRenamed: (TabId, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var tabToRename by remember { mutableStateOf<TabData?>(null) }
    var newTabName: String? by remember { mutableStateOf(null) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab row with scrollable tabs
            TabRow(
                selectedTabIndex = tabs.indexOfFirst { it.id == selectedTabId }.takeIf { it >= 0 } ?: 0,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = tab.id == selectedTabId,
                        onClick = { onTabSelected(tab.id) },
                        modifier = Modifier.height(48.dp),
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = tab.name ?: "Untilted",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    // Edit button
                                    IconButton(
                                        onClick = {
                                            tabToRename = tab
                                            newTabName = tab.name
                                            showRenameDialog = true
                                        },
                                        modifier = Modifier.size(16.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Rename tab",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                if (tab.templateData != null) {
                                    with(tab.templateData) {
                                        Text(
                                            text = templateFile.name.let {
                                                // Display '*' next to the template file name
                                                // if the definitions have changed
                                                if (definitionsHaveChanged) {
                                                    "$it *"
                                                } else it
                                            },
                                            maxLines = 1,
                                            fontStyle = FontStyle.Italic,
                                        )
                                    }
                                }

                                // Close button (only show if there's more than one tab)
                                if (tabs.size > 1) {
                                    IconButton(
                                        onClick = { onTabClosed(tab.id) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close tab",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Add new tab button
            IconButton(
                onClick = onTabAdded,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new tab"
                )
            }
        }
    }

    // Rename dialog
    if (showRenameDialog && tabToRename != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Tab") },
            text = {
                OutlinedTextField(
                    value = newTabName ?: "",
                    onValueChange = { newTabName = it },
                    label = { Text("Tab Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newTabName = newTabName
                        if (newTabName != null && newTabName.isNotBlank()) {
                            onTabRenamed(tabToRename!!.id, newTabName)
                        }
                        showRenameDialog = false
                    }
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
