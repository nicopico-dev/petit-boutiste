package fr.nicopico.petitboutiste

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import fr.nicopico.petitboutiste.ui.PetitBoutisteTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.TabData
import fr.nicopico.petitboutiste.ui.AppScreen
import fr.nicopico.petitboutiste.ui.TabBar
import fr.nicopico.petitboutiste.ui.infra.savers.TabIdSaver
import fr.nicopico.petitboutiste.ui.infra.savers.TabsSaver
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Initialize with a default tab
    var tabs by rememberSaveable(stateSaver = TabsSaver) {
        mutableStateOf(listOf(TabData()))
    }

    // Track the currently selected tab
    var selectedTabId by rememberSaveable(stateSaver = TabIdSaver) {
        mutableStateOf(tabs.first().id)
    }

    // Find the currently selected tab
    val selectedTabIndex = tabs.indexOfFirst { it.id == selectedTabId }.takeIf { it >= 0 } ?: 0
    val selectedTab = tabs.getOrNull(selectedTabIndex) ?: tabs.first()

    PetitBoutisteTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab bar for switching between tabs
            TabBar(
                tabs = tabs,
                selectedTabId = selectedTabId,
                onTabSelected = { tabId -> selectedTabId = tabId },
                onTabAdded = {
                    val newTab = TabData()
                    tabs = tabs + newTab
                    selectedTabId = newTab.id
                },
                onTabClosed = { tabId ->
                    if (tabs.size > 1) {
                        val tabIndex = tabs.indexOfFirst { it.id == tabId }
                        tabs = tabs.filterNot { it.id == tabId }

                        // If we closed the selected tab, select another one
                        if (tabId == selectedTabId) {
                            val newIndex = if (tabIndex >= tabs.size) tabs.size - 1 else tabIndex
                            selectedTabId = tabs[newIndex].id
                        }
                    }
                },
                onTabRenamed = { tabId, newName ->
                    tabs = tabs.map {
                        if (it.id == tabId) it.copy(name = newName) else it
                    }
                }
            )

            // Main app screen with the selected tab's data
            AppScreen(
                inputData = selectedTab.inputData,
                groupDefinitions = selectedTab.groupDefinitions,
                onInputDataChanged = { newData ->
                    tabs = tabs.map {
                        if (it.id == selectedTabId) it.copy(inputData = newData) else it
                    }
                },
                onGroupDefinitionsChanged = { newDefinitions ->
                    tabs = tabs.map {
                        if (it.id == selectedTabId) it.copy(groupDefinitions = newDefinitions) else it
                    }
                }
            )
        }
    }
}
