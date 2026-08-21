/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

import fr.nicopico.petitboutiste.Reducer
import fr.nicopico.petitboutiste.models.InputType
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.events.AppEvent
import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.models.state.AppState
import fr.nicopico.petitboutiste.models.state.TabData
import fr.nicopico.petitboutiste.models.state.TabDataRendering
import fr.nicopico.petitboutiste.models.state.TabsState
import fr.nicopico.petitboutiste.repository.TemplateManager
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import io.github.vinceglb.filekit.utils.toKotlinxIoPath
import kotlinx.coroutines.test.runTest
import kotlinx.io.files.Path
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ReducerTest {

    private val templateManager = object : TemplateManager {
        var lastTemplateLoaded: Path? = null
        var templateToReturn: Template? = null
        var lastTemplateSaved: Template? = null
        var lastSaveFile: Path? = null
        var lastOverwrite: Boolean = false

        override suspend fun loadTemplate(templateFilePath: Path): Template {
            lastTemplateLoaded = templateFilePath
            return templateToReturn ?: Template(name = "Default")
        }

        override suspend fun saveTemplate(template: Template, templateFilePath: Path, overwrite: Boolean) {
            lastTemplateSaved = template
            lastSaveFile = templateFilePath
            lastOverwrite = overwrite
        }
    }

    private val reducer = Reducer(templateManager)

    @Test
    fun `RefreshRenderingEvent updates all tabs`() = runTest {
        // Given
        val tab1 = TabData(rendering = TabDataRendering(inputData = HexString("AA")))
        val tab2 = TabData(rendering = TabDataRendering(inputData = HexString("BB")))
        val state = AppState(tabsState = TabsState(tabs = listOf(tab1, tab2), selectedTabId = tab1.id))

        // Initial state has no byte items
        assertEquals(0, state.tabsState.tabs[0].rendering.byteItems.size)
        assertEquals(0, state.tabsState.tabs[1].rendering.byteItems.size)

        // When
        val newState = reducer(state, AppEvent.RefreshRenderingEvent)

        // Then
        assertEquals(1, newState.tabsState.tabs[0].rendering.byteItems.size)
        assertEquals(1, newState.tabsState.tabs[1].rendering.byteItems.size)
        assertEquals("AA", (newState.tabsState.tabs[0].rendering.byteItems[0] as SingleByte).value)
        assertEquals("BB", (newState.tabsState.tabs[1].rendering.byteItems[0] as SingleByte).value)
    }

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
        assertEquals(2, newState.tabsState.tabs.size)
        assertEquals(newTabData.id, newState.tabsState.selectedTabId)
        assertEquals("New Tab", newState.tabsState.tabs.last().name)
    }

    @Test
    fun `SelectTabEvent changes the selected tab`() = runTest {
        // Given
        val tab1 = TabData()
        val tab2 = TabData()
        val initialState = AppState(tabsState = TabsState(tabs = listOf(tab1, tab2), selectedTabId = tab2.id))
        val event = AppEvent.SelectTabEvent(tab1.id)

        // When
        val newState = reducer(initialState, event)

        // Then
        assertEquals(tab1.id, newState.tabsState.selectedTabId)
    }

    @Test
    fun `RenameTabEvent updates the tab name`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.tabsState.selectedTabId
        val event = AppEvent.RenameTabEvent(tabId, "Renamed Tab")

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals("Renamed Tab", newState.tabsState.tabs.first { it.id == tabId }.name)
    }

    @Test
    fun `RemoveTabEvent removes a tab and handles selection`() = runTest {
        // Given
        val tab1 = TabData()
        val tab2 = TabData()
        val state = AppState(tabsState = TabsState(tabs = listOf(tab1, tab2), selectedTabId = tab2.id))
        val event = AppEvent.RemoveTabEvent(tab2.id)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(1, newState.tabsState.tabs.size)
        assertEquals(tab1.id, newState.tabsState.selectedTabId)
    }

    @Test
    fun `RemoveTabEvent adds a default tab if the last tab is removed`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.tabsState.selectedTabId
        val event = AppEvent.RemoveTabEvent(tabId)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(1, newState.tabsState.tabs.size)
        assertNotEquals(tabId, newState.tabsState.tabs.first().id)
    }

    @Test
    fun `DuplicateTabEvent copies the tab with a new ID`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.tabsState.selectedTabId
        val event = AppEvent.DuplicateTabEvent(tabId)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(2, newState.tabsState.tabs.size)
        assertNotEquals(tabId, newState.tabsState.selectedTabId)
        assertEquals(state.tabsState.selectedTab.inputData, newState.tabsState.selectedTab.inputData)
    }

    @Test
    fun `CycleTabEvent cycles between tabs`() = runTest {
        // Given
        val tab1 = TabData()
        val tab2 = TabData()
        val tab3 = TabData()
        val state = AppState(tabsState = TabsState(tabs = listOf(tab1, tab2, tab3), selectedTabId = tab1.id))

        // Cycle Forward
        val nextState = reducer(state, AppEvent.CycleTabEvent(cycleForward = true))
        assertEquals(tab2.id, nextState.tabsState.selectedTabId)

        // Cycle Forward from last
        val lastState = nextState.copy(tabsState = nextState.tabsState.copy(selectedTabId = tab3.id))
        val wrapState = reducer(lastState, AppEvent.CycleTabEvent(cycleForward = true))
        assertEquals(tab1.id, wrapState.tabsState.selectedTabId)

        // Cycle Backward
        val prevState = reducer(state, AppEvent.CycleTabEvent(cycleForward = false))
        assertEquals(tab3.id, prevState.tabsState.selectedTabId)
    }

    @Test
    fun `ChangeInputTypeEvent updates the input data type`() = runTest {
        // Given
        val state = AppState()
        val event = AppEvent.CurrentTabEvent.ChangeInputTypeEvent(InputType.BINARY)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(InputType.BINARY, newState.tabsState.selectedTab.inputData.inputType)
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
        assertEquals(newData, newState.tabsState.selectedTab.inputData)
    }

    @Test
    fun `AddDefinitionEvent adds and sorts definitions`() = runTest {
        // Given
        val state = AppState()
        val def1 = ByteGroupDefinition.createFromRange(
            indexes = 5..6,
            name = "Def 1",
        )
        val def2 = ByteGroupDefinition.createFromRange(
            indexes = 0..1,
            name = "Def 2",
        )

        // When
        var newState = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(def1))
        newState = reducer(newState, AppEvent.CurrentTabEvent.AddDefinitionEvent(def2))

        // Then
        // Sorted by resolved start index (static formulas)
        val definitions = newState.tabsState.selectedTab.groupDefinitions
        assertEquals(2, definitions.size)
        assertEquals("Def 2", definitions[0].name)
        assertEquals("Def 1", definitions[1].name)
    }

    @Test
    fun `UpdateDefinitionEvent updates an existing definition`() = runTest {
        // Given
        val def = ByteGroupDefinition.createFromRange(
            indexes = 0..1,
            name = "Original",
        )
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
        val updatedDef = def.copy(name = "Updated")
        val event = AppEvent.CurrentTabEvent.UpdateDefinitionEvent(def, updatedDef)

        // When
        val newState = reducer(initialState, event)

        // Then
        assertEquals("Updated", newState.tabsState.selectedTab.groupDefinitions.first().name)
    }

    @Test
    fun `DeleteDefinitionEvent removes a definition`() = runTest {
        // Given
        val def = ByteGroupDefinition.createFromRange(
            indexes = 0..1,
            name = "To Delete",
        )
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
        val event = AppEvent.CurrentTabEvent.DeleteDefinitionEvent(def)

        // When
        val newState = reducer(initialState, event)

        // Then
        assertTrue(newState.tabsState.selectedTab.groupDefinitions.isEmpty())
    }

    @Test
    fun `ClearAllDefinitionsEvent removes all definitions`() = runTest {
        // Given
        val def = ByteGroupDefinition.createFromRange(
            indexes = 0..1,
            name = "Def",
        )
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
        val event = AppEvent.CurrentTabEvent.ClearAllDefinitionsEvent

        // When
        val newState = reducer(initialState, event)

        // Then
        assertTrue(newState.tabsState.selectedTab.groupDefinitions.isEmpty())
    }

    @Test
    fun `UpdateScratchpadEvent updates the scratchpad`() = runTest {
        // Given
        val state = AppState()
        val event = AppEvent.CurrentTabEvent.UpdateScratchpadEvent("New Notes")

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals("New Notes", newState.tabsState.selectedTab.scratchpad)
    }

    @Test
    fun `LoadTemplateEvent loads definitions and scratchpad by default`() = runTest {
        // Given
        val state = AppState()
        val templateFile = File("template.json")
        val templateFilePath = templateFile.toKotlinxIoPath()
        val template = Template(
            name = "Test Template",
            definitions = listOf(
                ByteGroupDefinition.createFromRange(0..1, "Template Def")
            ),
            scratchpad = "Template Scratchpad"
        )
        templateManager.templateToReturn = template

        // When
        val newState = reducer(
            state = state,
            event = AppEvent.CurrentTabEvent.LoadTemplateEvent(
                templateFilePath = templateFilePath,
                definitionsOnly = false,
            )
        )

        // Then
        with(newState.tabsState.selectedTab) {
            assertEquals(1, groupDefinitions.size)
            assertEquals("Template Def", groupDefinitions.first().name)
            assertEquals("Template Scratchpad", scratchpad)
            assertEquals(templateFilePath, templateData?.templateFilePath)
        }
    }

    @Test
    fun `LoadTemplateEvent loads definitions only with definitionsOnly flag`() = runTest {
        // Given
        val state = AppState(
            tabsState = TabsState(
                tabs = listOf(
                    TabData(
                        scratchpad = "Original Scratchpad",
                    )
                )
            )
        )
        val templateFile = File("template.json")
        val templateFilePath = templateFile.toKotlinxIoPath()
        val template = Template(
            name = "Test Template",
            definitions = listOf(
                ByteGroupDefinition.createFromRange(0..1, "Template Def")
            ),
            scratchpad = "Template Scratchpad"
        )
        templateManager.templateToReturn = template

        // When
        val newState = reducer(
            state = state,
            event = AppEvent.CurrentTabEvent.LoadTemplateEvent(
                templateFilePath = templateFilePath,
                definitionsOnly = true,
            )
        )

        // Then
        with(newState.tabsState.selectedTab) {
            assertEquals(1, groupDefinitions.size)
            assertEquals("Template Def", groupDefinitions.first().name)
            assertEquals("Original Scratchpad", scratchpad)
            assertEquals(templateFilePath, templateData?.templateFilePath)
        }
    }

    @Test
    fun `SaveTemplateEvent saves the template`() = runTest {
        // Given
        val state = AppState()
        val templateFile = File("save.json")
        val templateFilePath = templateFile.toKotlinxIoPath()
        val event = AppEvent.CurrentTabEvent.SaveTemplateEvent(templateFilePath, updateExisting = true)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(templateFilePath, templateManager.lastSaveFile)
        assertTrue(templateManager.lastOverwrite)
        assertEquals(templateFilePath, newState.tabsState.selectedTab.templateData?.templateFilePath)
    }

    @Test
    fun `AddDefinitionsFromTemplateEvent adds definitions to existing ones`() = runTest {
        // Given
        val existingDef = ByteGroupDefinition.createFromRange(
            indexes = 0..1,
            name = "Existing",
        )
        val initialState = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(existingDef))

        val templateFile = File("extra.json")
        val templateFilePath = templateFile.toKotlinxIoPath()
        val template = Template(
            name = "Extra",
            definitions = listOf(
                ByteGroupDefinition.createFromRange(
                    indexes = 2..3,
                    name = "Extra Def",
                )
            )
        )
        templateManager.templateToReturn = template

        // When
        val newState = reducer(
            initialState,
            AppEvent.CurrentTabEvent.AddDefinitionsFromTemplateEvent(templateFilePath)
        )

        // Then
        with(newState.tabsState.selectedTab) {
            assertEquals(2, groupDefinitions.size)
            assertTrue(groupDefinitions.any { it.name == "Existing" })
            assertTrue(groupDefinitions.any { it.name == "Extra Def" })
        }
    }

    @Test
    fun `DuplicateTabEvent handles multiple duplications correctly`() = runTest {
        // Given
        val state = AppState()
        val tabId = state.tabsState.selectedTabId
        val event = AppEvent.DuplicateTabEvent(tabId)

        // When
        val state2 = reducer(state, event)
        val state3 = reducer(state2, event)

        // Then
        assertEquals(3, state3.tabsState.tabs.size)
        // All tabs have unique IDs
        assertEquals(3, state3.tabsState.tabs.map { it.id }.distinct().size)
    }

    @Test
    fun `AddDefinitionEvent sorts definitions by start index`() = runTest {
        // Given
        val state = AppState()
        val def1 = ByteGroupDefinition.createFromRange(indexes = 10..11, name = "Later")
        val def2 = ByteGroupDefinition.createFromRange(indexes = 0..1, name = "First")
        val def3 = ByteGroupDefinition.createFromRange(indexes = 5..6, name = "Middle")

        // When
        var newState = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(def1))
        newState = reducer(newState, AppEvent.CurrentTabEvent.AddDefinitionEvent(def2))
        newState = reducer(newState, AppEvent.CurrentTabEvent.AddDefinitionEvent(def3))

        // Then
        val definitions = newState.tabsState.selectedTab.groupDefinitions
        assertEquals(3, definitions.size)
        assertEquals("First", definitions[0].name)
        assertEquals("Middle", definitions[1].name)
        assertEquals("Later", definitions[2].name)
    }

    @Test
    fun `overlapping definitions store processing error in rendering state`() = runTest {
        // Given
        val payload = HexString("1A2B3C4D")
        val stateWithInput = reducer(
            AppState(),
            AppEvent.CurrentTabEvent.ChangeInputDataEvent(payload)
        )

        val first = ByteGroupDefinition.createFromRange(indexes = 0..2, name = "First")
        val overlapping = ByteGroupDefinition.createFromRange(indexes = 1..3, name = "Overlapping")

        // When
        val stateAfterFirst = reducer(stateWithInput, AppEvent.CurrentTabEvent.AddDefinitionEvent(first))
        val stateAfterSecond = reducer(stateAfterFirst, AppEvent.CurrentTabEvent.AddDefinitionEvent(overlapping))

        // Then
        val errors = stateAfterSecond.tabsState.selectedTab.rendering.errors
        val overlapError = errors[overlapping.id]
        assertTrue(overlapError != null)
        assertTrue(overlapError.contains("Overlap detected"))
        assertTrue(overlapError.contains("1"))

        val groups = stateAfterSecond.tabsState.selectedTab.rendering.byteItems.filterIsInstance<ByteGroup>()
        assertEquals(1, groups.size)
        assertEquals(first.id, groups.single().definition.id)
        assertTrue(groups.none { it.definition.id == overlapping.id })
    }

    @Test
    fun `UpdateDefinitionEvent maintains sort order`() = runTest {
        // Given
        val def1 = ByteGroupDefinition.createFromRange(indexes = 0..1, name = "Def 1")
        val def2 = ByteGroupDefinition.createFromRange(indexes = 5..6, name = "Def 2")
        var state = reducer(AppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def1))
        state = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(def2))

        // When - update first definition to start after the second one
        val updatedDef1 = def1.copy(
            startFormula = "10",
            endFormula = "11",
        )
        val newState = reducer(state, AppEvent.CurrentTabEvent.UpdateDefinitionEvent(def1, updatedDef1))

        // Then
        // Sorted by resolved start index (static formulas)
        val definitions = newState.tabsState.selectedTab.groupDefinitions
        assertEquals("Def 2", definitions[0].name)
        assertEquals("Def 1", definitions[1].name)
    }

    @Test
    fun `UndoRemoveTabEvent restores removed tab`() = runTest {
        // Given
        val tab1 = TabData(name = "Tab 1")
        val tab2 = TabData(name = "Tab 2")
        val state = AppState(tabsState = TabsState(tabs = listOf(tab1), selectedTabId = tab1.id))
        val event = AppEvent.UndoRemoveTabEvent(tab2, 1)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(2, newState.tabsState.tabs.size)
        assertEquals(tab2.id, newState.tabsState.tabs[1].id)
        assertEquals(tab2.id, newState.tabsState.selectedTabId)
    }

    @Test
    fun `UndoClearAllDefinitionsEvent restores definitions`() = runTest {
        // Given
        val defs = listOf(
            ByteGroupDefinition.createFromRange(0..1, "Test")
        )
        val tab = TabData()
        val state = AppState(tabsState = TabsState(tabs = listOf(tab), selectedTabId = tab.id))
        val rendering = TabDataRendering(groupDefinitions = defs)
        val event = AppEvent.CurrentTabEvent.UndoClearAllDefinitionsEvent(tab.id, rendering, null)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(defs, newState.tabsState.selectedTab.groupDefinitions)
    }
}
