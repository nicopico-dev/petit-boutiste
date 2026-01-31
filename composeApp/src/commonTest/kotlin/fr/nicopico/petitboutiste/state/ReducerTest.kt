/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.repository.TemplateManager
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ReducerTest {

    private val templateManager = object : TemplateManager {
        var lastTemplateLoaded: File? = null
        var templateToReturn: Template? = null
        var lastTemplateSaved: Template? = null
        var lastSaveFile: File? = null
        var lastOverwrite: Boolean = false

        override suspend fun loadTemplate(templateFile: File): Template {
            lastTemplateLoaded = templateFile
            return templateToReturn ?: Template(name = "Default")
        }

        override suspend fun saveTemplate(template: Template, templateFile: File, overwrite: Boolean) {
            lastTemplateSaved = template
            lastSaveFile = templateFile
            lastOverwrite = overwrite
        }
    }

    private val reducer = Reducer(templateManager)

    @Test
    fun `SwitchAppThemeEvent updates theme`() = runTest {
        // Given
        val state = AppState()
        val event = AppEvent.SwitchAppThemeEvent(PBTheme.Dark)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(PBTheme.Dark, newState.appTheme)
    }

    @Test
    fun `AddNewTabEvent adds a new tab and selects it`() = runTest {
        // Given
        val state = AppState()
        val newTabData = TabData(name = "New Tab")
        val event = AppEvent.AddNewTabEvent(newTabData)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(2, newState.tabs.size)
        assertEquals(newTabData.id, newState.selectedTabId)
        assertEquals("New Tab", newState.tabs.last().name)
    }

    @Test
    fun `SelectTabEvent changes the selected tab`() = runTest {
        // Given
        val tab1 = TabData()
        val tab2 = TabData()
        val initialState = AppState(tabs = listOf(tab1, tab2), selectedTabId = tab2.id)
        val event = AppEvent.SelectTabEvent(tab1.id)

        // When
        val newState = reducer(initialState, event)

        // Then
        assertEquals(tab1.id, newState.selectedTabId)
    }

    @Test
    fun `RenameTabEvent updates the tab name`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.selectedTabId
        val event = AppEvent.RenameTabEvent(tabId, "Renamed Tab")

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals("Renamed Tab", newState.tabs.first { it.id == tabId }.name)
    }

    @Test
    fun `RemoveTabEvent removes a tab and handles selection`() = runTest {
        // Given
        val tab1 = TabData()
        val tab2 = TabData()
        val state = AppState(tabs = listOf(tab1, tab2), selectedTabId = tab2.id)
        val event = AppEvent.RemoveTabEvent(tab2.id)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(1, newState.tabs.size)
        assertEquals(tab1.id, newState.selectedTabId)
    }

    @Test
    fun `RemoveTabEvent adds a default tab if the last tab is removed`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.selectedTabId
        val event = AppEvent.RemoveTabEvent(tabId)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(1, newState.tabs.size)
        assertNotEquals(tabId, newState.tabs.first().id)
    }

    @Test
    fun `DuplicateTabEvent copies the tab with a new ID`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.selectedTabId
        val event = AppEvent.DuplicateTabEvent(tabId)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(2, newState.tabs.size)
        assertNotEquals(tabId, newState.selectedTabId)
        assertEquals(state.selectedTab.inputData, newState.selectedTab.inputData)
    }

    @Test
    fun `CycleTabEvent cycles between tabs`() = runTest {
        // Given
        val tab1 = TabData()
        val tab2 = TabData()
        val tab3 = TabData()
        val state = AppState(tabs = listOf(tab1, tab2, tab3), selectedTabId = tab1.id)

        // Cycle Forward
        val nextState = reducer(state, AppEvent.CycleTabEvent(cycleForward = true))
        assertEquals(tab2.id, nextState.selectedTabId)

        // Cycle Forward from last
        val lastState = nextState.copy(selectedTabId = tab3.id)
        val wrapState = reducer(lastState, AppEvent.CycleTabEvent(cycleForward = true))
        assertEquals(tab1.id, wrapState.selectedTabId)

        // Cycle Backward
        val prevState = reducer(state, AppEvent.CycleTabEvent(cycleForward = false))
        assertEquals(tab3.id, prevState.selectedTabId)
    }

    @Test
    fun `ChangeInputTypeEvent updates the input data type`() = runTest {
        // Given
        val state = AppState()
        val event = AppEvent.CurrentTabEvent.ChangeInputTypeEvent(InputType.BINARY)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(InputType.BINARY, newState.selectedTab.inputData.inputType)
    }

    @Test
    fun `ChangeInputDataEvent updates the input data`() = runTest {
        // Given
        val state = AppState()
        val newData = HexString("AABB")
        val event = AppEvent.CurrentTabEvent.ChangeInputDataEvent(newData)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(newData, newState.selectedTab.inputData)
    }

    @Test
    fun `AddDefinitionEvent adds and sorts definitions`() = runTest {
        // Given
        val state = AppState()
        val def1 = ByteGroupDefinition(indexes = 5..6, name = "Def 1")
        val def2 = ByteGroupDefinition(indexes = 0..1, name = "Def 2")

        // When
        var newState = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(def1))
        newState = reducer(newState, AppEvent.CurrentTabEvent.AddDefinitionEvent(def2))

        // Then
        val definitions = newState.selectedTab.groupDefinitions
        assertEquals(2, definitions.size)
        assertEquals("Def 2", definitions[0].name) // Sorted by index
        assertEquals("Def 1", definitions[1].name)
    }

    @Test
    fun `UpdateDefinitionEvent updates an existing definition`() = runTest {
        // Given
        val def = ByteGroupDefinition(indexes = 0..1, name = "Original")
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
        val updatedDef = def.copy(name = "Updated")
        val event = AppEvent.CurrentTabEvent.UpdateDefinitionEvent(def, updatedDef)

        // When
        val newState = reducer(initialState, event)

        // Then
        assertEquals("Updated", newState.selectedTab.groupDefinitions.first().name)
    }

    @Test
    fun `DeleteDefinitionEvent removes a definition`() = runTest {
        // Given
        val def = ByteGroupDefinition(indexes = 0..1, name = "To Delete")
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
        val event = AppEvent.CurrentTabEvent.DeleteDefinitionEvent(def)

        // When
        val newState = reducer(initialState, event)

        // Then
        assertTrue(newState.selectedTab.groupDefinitions.isEmpty())
    }

    @Test
    fun `ClearAllDefinitionsEvent removes all definitions`() = runTest {
        // Given
        val def = ByteGroupDefinition(indexes = 0..1, name = "Def")
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
        val event = AppEvent.CurrentTabEvent.ClearAllDefinitionsEvent

        // When
        val newState = reducer(initialState, event)

        // Then
        assertTrue(newState.selectedTab.groupDefinitions.isEmpty())
    }

    @Test
    fun `UpdateScratchpadEvent updates the scratchpad`() = runTest {
        // Given
        val state = AppState()
        val event = AppEvent.CurrentTabEvent.UpdateScratchpadEvent("New Notes")

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals("New Notes", newState.selectedTab.scratchpad)
    }

    @Test
    fun `LoadTemplateEvent loads definitions and scratchpad`() = runTest {
        // Given
        val state = AppState()
        val templateFile = File("template.json")
        val template = Template(
            name = "Test Template",
            definitions = listOf(ByteGroupDefinition(0..1, "Template Def")),
            scratchpad = "Template Scratchpad"
        )
        templateManager.templateToReturn = template

        // When - Definitions only = false
        val newState = reducer(state, AppEvent.CurrentTabEvent.LoadTemplateEvent(templateFile, definitionsOnly = false))

        // Then
        assertEquals(1, newState.selectedTab.groupDefinitions.size)
        assertEquals("Template Def", newState.selectedTab.groupDefinitions.first().name)
        assertEquals("Template Scratchpad", newState.selectedTab.scratchpad)
        assertEquals(templateFile, newState.selectedTab.templateData?.templateFile)

        // When - Definitions only = true
        val stateWithScratchpad = state.updateCurrentTab { copy(scratchpad = "Original Scratchpad") }
        val newStateDefinitionsOnly = reducer(stateWithScratchpad, AppEvent.CurrentTabEvent.LoadTemplateEvent(templateFile, definitionsOnly = true))

        // Then
        assertEquals("Original Scratchpad", newStateDefinitionsOnly.selectedTab.scratchpad)
    }

    @Test
    fun `SaveTemplateEvent saves the template`() = runTest {
        // Given
        val state = AppState()
        val templateFile = File("save.json")
        val event = AppEvent.CurrentTabEvent.SaveTemplateEvent(templateFile, updateExisting = true)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(templateFile, templateManager.lastSaveFile)
        assertTrue(templateManager.lastOverwrite)
        assertEquals(templateFile, newState.selectedTab.templateData?.templateFile)
    }

    @Test
    fun `AddDefinitionsFromTemplateEvent adds definitions to existing ones`() = runTest {
        // Given
        val existingDef = ByteGroupDefinition(indexes = 0..1, name = "Existing")
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(existingDef))
        
        val templateFile = File("extra.json")
        val template = Template(
            name = "Extra",
            definitions = listOf(ByteGroupDefinition(indexes = 2..3, name = "Extra Def"))
        )
        templateManager.templateToReturn = template

        // When
        val newState = reducer(initialState, AppEvent.CurrentTabEvent.AddDefinitionsFromTemplateEvent(templateFile))

        // Then
        assertEquals(2, newState.selectedTab.groupDefinitions.size)
        assertTrue(newState.selectedTab.groupDefinitions.any { it.name == "Existing" })
        assertTrue(newState.selectedTab.groupDefinitions.any { it.name == "Extra Def" })
    }

    @Test
    fun `DuplicateTabEvent handles multiple duplications correctly`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.selectedTabId
        val event = AppEvent.DuplicateTabEvent(tabId)

        // When
        val state2 = reducer(state, event)
        val state3 = reducer(state2, event)

        // Then
        assertEquals(3, state3.tabs.size)
        // All tabs have unique IDs
        assertEquals(3, state3.tabs.map { it.id }.distinct().size)
    }

    @Test
    fun `AddDefinitionEvent sorts definitions by start index`() = runTest {
        // Given
        val state = AppState()
        val def1 = ByteGroupDefinition(indexes = 10..11, name = "Later")
        val def2 = ByteGroupDefinition(indexes = 0..1, name = "First")
        val def3 = ByteGroupDefinition(indexes = 5..6, name = "Middle")

        // When
        var newState = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(def1))
        newState = reducer(newState, AppEvent.CurrentTabEvent.AddDefinitionEvent(def2))
        newState = reducer(newState, AppEvent.CurrentTabEvent.AddDefinitionEvent(def3))

        // Then
        val definitions = newState.selectedTab.groupDefinitions
        assertEquals(3, definitions.size)
        assertEquals("First", definitions[0].name)
        assertEquals("Middle", definitions[1].name)
        assertEquals("Later", definitions[2].name)
    }

    @Test
    fun `UpdateDefinitionEvent maintains sort order`() = runTest {
        // Given
        val def1 = ByteGroupDefinition(indexes = 0..1, name = "Def 1")
        val def2 = ByteGroupDefinition(indexes = 5..6, name = "Def 2")
        var state = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def1))
        state = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(def2))

        // When - update first definition to start after the second one
        val updatedDef1 = def1.copy(indexes = 10..11)
        val newState = reducer(state, AppEvent.CurrentTabEvent.UpdateDefinitionEvent(def1, updatedDef1))

        // Then
        val definitions = newState.selectedTab.groupDefinitions
        assertEquals("Def 2", definitions[0].name)
        assertEquals("Def 1", definitions[1].name)
    }

    private fun AppState.updateCurrentTab(block: TabData.() -> TabData): AppState {
        return copy(tabs = tabs.map { if (it.id == selectedTabId) it.block() else it })
    }
}
