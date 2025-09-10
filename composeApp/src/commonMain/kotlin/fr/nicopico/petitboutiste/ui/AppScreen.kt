package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppScreen(
    tabs: List<TabData>,
    selectedTabId: TabId,
    onAppEvent: (AppEvent) -> Unit,
) {
    val selectedTab by remember(tabs, selectedTabId) {
        derivedStateOf { tabs.first { it.id == selectedTabId } }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Main app screen with the selected tab's data
        TabContent(
            inputData = selectedTab.inputData,
            groupDefinitions = selectedTab.groupDefinitions,
            inputType = selectedTab.inputType,
            onInputDataChanged = { newData ->
                onAppEvent(AppEvent.CurrentTabEvent.ChangeInputDataEvent(newData))
            },
            onGroupDefinitionsChanged = { newDefinitions ->
                onAppEvent(AppEvent.CurrentTabEvent.ChangeDefinitionsEvent(newDefinitions))
            },
            onInputTypeChanged = { newInputType ->
                onAppEvent(AppEvent.CurrentTabEvent.ChangeInputTypeEvent(newInputType))
            }
        )
    }
}

@Preview
@Composable
private fun AppScreenPreview() {
    val tabs = listOf(
        TabData(
            name = "Tab 1",
            inputData = HexString("FF0085DE"),
            groupDefinitions = listOf(
                ByteGroupDefinition(
                    indexes = 0..1,
                    name = "group 1",
                ),
                ByteGroupDefinition(
                    indexes = 2..2,
                    name = "group 2",
                ),
            )
        ),
        TabData(name = "Tab 2"),
    )
    WrapForPreview {
        AppScreen(
            tabs = tabs,
            selectedTabId = tabs.first().id,
            onAppEvent = {},
        )
    }
}
