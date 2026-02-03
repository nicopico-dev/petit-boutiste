/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import fr.nicopico.petitboutiste.state.TabsState
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider

@Composable
fun FrameWindowScope.PBMenuBar(
    tabsState: TabsState,
) {
    val currentTab = tabsState.selectedTab
    val menuActions = rememberMenuActions(tabsState)

    val warningIcon by rememberResourcePainterProvider(
        AllIconsKeys.General.Warning
    ).getPainter()

    MenuBar {
        Menu("File", mnemonic = 'F') {
            Item(
                text = "New Tab",
                shortcut = KeyShortcut(Key.T, meta = true),
                onClick = { menuActions.addNewTab() }
            )
            Item(
                text = "Duplicate Tab",
                shortcut = KeyShortcut(Key.T, meta = true, shift = true),
                onClick = { menuActions.duplicateTab(currentTab.id) }
            )
            Item(
                text = "Close Tab",
                icon = warningIcon,
                shortcut = KeyShortcut(Key.W, meta = true),
                onClick = { menuActions.removeTab(currentTab.id) }
            )
        }
        Menu("Template", mnemonic = 'T') {
            Item(
                text = "Load template",
                shortcut = KeyShortcut(Key.L, meta = true),
                onClick = { menuActions.loadTemplate() }
            )
            Item(
                text = "Save template",
                shortcut = KeyShortcut(Key.S, meta = true),
                onClick = { menuActions.saveTemplate(currentTab) }
            )
            Item(
                text = "Save template as ...",
                shortcut = KeyShortcut(Key.S, meta = true, shift = true),
                onClick = { menuActions.saveTemplateAs(currentTab) }
            )
        }

        Menu("Definitions", mnemonic = 'D') {
            Item(
                text = "Restore definitions from current template",
                enabled = currentTab.templateData?.definitionsHaveChanged == true,
                onClick = { menuActions.restoreDefinitions(currentTab) }
            )

            Item(
                text = "Add definitions from another template",
                onClick = { menuActions.addDefinitionsFromAnotherTemplate() }
            )

            Separator()

            Item(
                text = "Clear all definitions",
                icon = warningIcon,
                onClick = { menuActions.clearAllDefinitions() }
            )
        }
    }
}
