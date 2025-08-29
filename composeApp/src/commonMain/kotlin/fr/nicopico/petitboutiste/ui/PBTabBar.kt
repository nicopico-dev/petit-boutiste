package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.TabContentScope
import org.jetbrains.jewel.ui.component.TabState
import org.jetbrains.jewel.ui.component.TabStrip
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.styling.TabStyle
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.theme.defaultTabStyle
import org.jetbrains.jewel.ui.component.TabData as JewelTabData

@Composable
fun PBTabBar(
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

    val jewelTabs = tabs.map { tabData ->
        JewelTabData.Default(
            selected = tabData.id == selectedTabId,
            content = { tabState ->
                TabContent(
                    tab = tabData,
                    state = tabState,
                    onClickRename = {
                        tabToRename = tabData
                        newTabName = tabData.name
                        showRenameDialog = true
                    }
                )
            },
            closable = tabs.size > 1,
            onClick = { onTabSelected(tabData.id) },
            onClose = { onTabClosed(tabData.id) },
        )
    }

    TabStripWithAddButton(
        tabs = jewelTabs,
        style = JewelTheme.defaultTabStyle,
        onAddClick = onTabAdded,
        modifier = modifier,
    )

    // Rename dialog
    if (showRenameDialog && tabToRename != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { androidx.compose.material3.Text("Rename Tab") },
            text = {
                OutlinedTextField(
                    value = newTabName ?: "",
                    onValueChange = { newTabName = it },
                    label = { androidx.compose.material3.Text("Tab Name") },
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
                    androidx.compose.material3.Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TabStripWithAddButton(
    tabs: List<JewelTabData>,
    style: TabStyle,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        TabStrip(tabs, style, Modifier.weight(1f))

        IconButton(
            content = {
                Icon(
                    key = AllIconsKeys.General.Add,
                    contentDescription = "Add new tab",
                )
            },
            modifier = Modifier.size(JewelTheme.defaultTabStyle.metrics.tabHeight),
            onClick = { onAddClick() },
        )
    }
}

@Composable
private fun TabContentScope.TabContent(
    tab: TabData,
    state: TabState,
    onClickRename: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.tabContentAlpha(state),
    ) {
        Icon(
            key = AllIconsKeys.FileTypes.BinaryData,
            contentDescription = null,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = tab.name ?: "Untitled",
            )

            Spacer(Modifier.width(8.dp))

            // Edit button
            IconButton(
                onClick = onClickRename,
                modifier = Modifier.size(16.dp),
            ) {
                Icon(
                    key = AllIconsKeys.Actions.Edit,
                    contentDescription = "Rename tab",
                    modifier = Modifier.size(JewelTheme.defaultTabStyle.metrics.tabHeight).tabContentAlpha(state),
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
                    color = Color.Gray,
                )
            }
        }
    }
}
