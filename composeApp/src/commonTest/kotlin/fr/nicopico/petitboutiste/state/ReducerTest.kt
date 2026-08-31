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
import fr.nicopico.petitboutiste.models.definition.name
import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.EndiannessArgument
import fr.nicopico.petitboutiste.models.state.AppState
import fr.nicopico.petitboutiste.models.state.TabData
import fr.nicopico.petitboutiste.models.state.TabDataRendering
import fr.nicopico.petitboutiste.models.state.TabId
import fr.nicopico.petitboutiste.models.state.TabsState
import fr.nicopico.petitboutiste.models.state.events.AppEvent
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

    private fun createAppState(
        tabs: List<TabData> = listOf(TabData()),
        selectedTabId: TabId = tabs.first().id,
        appTheme: PBTheme = PBTheme.System,
    ): AppState = AppState(
        tabsState = TabsState(tabs = tabs, selectedTabId = selectedTabId),
        appTheme = appTheme,
    )

    @Test
    fun `RefreshRenderingEvent updates all tabs`() = runTest {
        // Given
        val tab1 = TabData(rendering = TabDataRendering(inputData = HexString("AA")))
        val tab2 = TabData(rendering = TabDataRendering(inputData = HexString("BB")))
        val state = createAppState(tabs = listOf(tab1, tab2))

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
        val state = createAppState()
        val event = AppEvent.SwitchAppThemeEvent(PBTheme.Dark)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(PBTheme.Dark, newState.appTheme)
    }

    @Test
    fun `AddNewTabEvent adds a new tab and selects it`() = runTest {
        // Given
        val state = createAppState()
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
        val initialState = createAppState(tabs = listOf(tab1, tab2), selectedTabId = tab2.id)
        val event = AppEvent.SelectTabEvent(tab1.id)

        // When
        val newState = reducer(initialState, event)

        // Then
        assertEquals(tab1.id, newState.tabsState.selectedTabId)
    }

    @Test
    fun `RenameTabEvent updates the tab name`() = runTest {
        // Given
        val state = createAppState()
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
        val state = createAppState(tabs = listOf(tab1, tab2), selectedTabId = tab2.id)
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
        val state = createAppState()
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
        val state = createAppState()
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
        val state = createAppState(tabs = listOf(tab1, tab2, tab3))

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
        val state = createAppState()
        val event = AppEvent.CurrentTabEvent.ChangeInputTypeEvent(InputType.BINARY)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(InputType.BINARY, newState.tabsState.selectedTab.inputData.inputType)
    }

    @Test
    fun `ChangeInputDataEvent updates the input data`() = runTest {
        // Given
        val state = createAppState()
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
        val state = createAppState()
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
        val initialState = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
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
        val initialState = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
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
        val initialState = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def))
        val event = AppEvent.CurrentTabEvent.ClearAllDefinitionsEvent

        // When
        val newState = reducer(initialState, event)

        // Then
        assertTrue(newState.tabsState.selectedTab.groupDefinitions.isEmpty())
    }

    @Test
    fun `UpdateScratchpadEvent updates the scratchpad`() = runTest {
        // Given
        val state = createAppState()
        val event = AppEvent.CurrentTabEvent.UpdateScratchpadEvent("New Notes")

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals("New Notes", newState.tabsState.selectedTab.scratchpad)
    }

    @Test
    fun `LoadTemplateEvent loads definitions and scratchpad by default`() = runTest {
        // Given
        val state = createAppState()
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
        val state = createAppState(
            tabs = listOf(
                TabData(
                    scratchpad = "Original Scratchpad",
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
        val state = createAppState()
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
        val initialState = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(existingDef))

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
        val state = createAppState()
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
        val state = createAppState()
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
    fun `AddDefinitionEvent sorts definitions by resolved start value`() = runTest {
        // Given
        val state = reducer(
            createAppState(),
            AppEvent.CurrentTabEvent.ChangeInputDataEvent(HexString("0102030405")),
        )
        val defConstant = ByteGroupDefinition.createFromRange(indexes = 2..3, name = "A")
        // Resolves to 0, i.e. before "A"
        val defVariable = ByteGroupDefinition(
            startFormula = "[[A.start]] - 2",
            endFormula = "[[start]]",
            name = "B",
        )

        // When
        var newState = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(defConstant))
        newState = reducer(newState, AppEvent.CurrentTabEvent.AddDefinitionEvent(defVariable))

        // Then
        val definitions = newState.tabsState.selectedTab.groupDefinitions
        assertEquals(listOf("B", "A"), definitions.map { it.name })
    }

    @Test
    fun `ChangeInputDataEvent sorts definitions by resolved start value`() = runTest {
        // Given a definition starting on the last byte of the payload
        val defLastByte = ByteGroupDefinition(
            startFormula = "[[LAST]]",
            endFormula = "[[start]]",
            name = "Last",
        )
        val defMiddle = ByteGroupDefinition.createFromRange(indexes = 2..2, name = "Middle")

        var state = reducer(
            createAppState(),
            AppEvent.CurrentTabEvent.ChangeInputDataEvent(HexString("0102030405")),
        )
        state = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(defLastByte))
        state = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(defMiddle))

        // "Last" resolves to 4 with a 5-byte payload
        assertEquals(
            listOf("Middle", "Last"),
            state.tabsState.selectedTab.groupDefinitions.map { it.name },
        )

        // When the payload is shortened, "Last" resolves to 1
        val newState = reducer(
            state,
            AppEvent.CurrentTabEvent.ChangeInputDataEvent(HexString("0102")),
        )

        // Then
        assertEquals(
            listOf("Last", "Middle"),
            newState.tabsState.selectedTab.groupDefinitions.map { it.name },
        )
    }

    @Test
    fun `LoadTemplateEvent sorts definitions by resolved start value`() = runTest {
        // Given
        val state = createAppState()
        val templateFilePath = File("template.json").toKotlinxIoPath()
        templateManager.templateToReturn = Template(
            name = "Test Template",
            definitions = listOf(
                ByteGroupDefinition.createFromRange(5..6, "Second"),
                ByteGroupDefinition.createFromRange(0..1, "First"),
            ),
        )

        // When
        val newState = reducer(
            state = state,
            event = AppEvent.CurrentTabEvent.LoadTemplateEvent(
                templateFilePath = templateFilePath,
                definitionsOnly = false,
            )
        )

        // Then
        assertEquals(
            listOf("First", "Second"),
            newState.tabsState.selectedTab.groupDefinitions.map { it.name },
        )
    }

    @Test
    fun `overlapping definitions store processing error in rendering state`() = runTest {
        // Given
        val payload = HexString("1A2B3C4D")
        val stateWithInput = reducer(
            createAppState(),
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
        var state = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(def1))
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
        val state = createAppState(tabs = listOf(tab1))
        val event = AppEvent.UndoRemoveTabEvent(tab2, 1)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(2, newState.tabsState.tabs.size)
        assertEquals(tab2.id, newState.tabsState.tabs[1].id)
        assertEquals(tab2.id, newState.tabsState.selectedTabId)
    }

    @Test
    fun `AppendDefaultDefinitionEvent adds a definition starting at 0 when there are no definitions`() = runTest {
        // Given no definitions
        val state = createAppState()

        // When
        val newState = reducer(state, AppEvent.CurrentTabEvent.AppendDefaultDefinitionEvent)

        // Then a single default definition covering byte 0 is added
        val definitions = newState.tabsState.selectedTab.groupDefinitions
        assertEquals(1, definitions.size)
        assertEquals("0", definitions.first().startFormula)
        assertEquals("0", definitions.first().endFormula)
    }

    @Test
    fun `AppendDefaultDefinitionEvent adds a definition right after the last one`() = runTest {
        // Given an existing definition covering bytes 0..2
        val existingDef = ByteGroupDefinition.createFromRange(indexes = 0..2, name = "Existing")
        val initialState = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(existingDef))

        // When
        val newState = reducer(initialState, AppEvent.CurrentTabEvent.AppendDefaultDefinitionEvent)

        // Then the new definition starts right after the resolved end of the last one (byte 3)
        val definitions = newState.tabsState.selectedTab.groupDefinitions
        assertEquals(2, definitions.size)
        val appended = definitions.last { it.id != existingDef.id }
        assertEquals("3", appended.startFormula)
        assertEquals("3", appended.endFormula)
    }

    @Test
    fun `AppendDefaultDefinitionEvent reuses the last definition's representation`() = runTest {
        // Given an existing definition with a non-default representation
        val existingDef = ByteGroupDefinition.createFromRange(
            indexes = 0..0,
            name = "Existing",
            representation = Representation(dataRenderer = DataRenderer.Binary),
        )
        val initialState = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(existingDef))

        // When
        val newState = reducer(initialState, AppEvent.CurrentTabEvent.AppendDefaultDefinitionEvent)

        // Then the newly appended definition uses the same representation
        val appended = newState.tabsState.selectedTab.groupDefinitions.last { it.id != existingDef.id }
        assertEquals(DataRenderer.Binary, appended.representation.dataRenderer)
    }

    @Test
    fun `DuplicateDefinitionEvent appends a shifted copy right after the source definition`() = runTest {
        // Given an existing definition covering bytes 0..2
        val existingDef = ByteGroupDefinition.createFromRange(indexes = 0..2, name = "Original")
        val initialState = reducer(createAppState(), AppEvent.CurrentTabEvent.AddDefinitionEvent(existingDef))

        // When
        val newState = reducer(initialState, AppEvent.CurrentTabEvent.DuplicateDefinitionEvent(existingDef))

        // Then a new definition of the same length (3 bytes) is appended right after the original (bytes 3..5)
        val definitions = newState.tabsState.selectedTab.groupDefinitions
        assertEquals(2, definitions.size)
        val duplicated = definitions.last { it.id != existingDef.id }
        assertEquals("3", duplicated.startFormula)
        assertEquals("5", duplicated.endFormula)
        assertEquals("Original 2", duplicated.name)
    }

    @Test
    fun `DuplicateDefinitionEvent resolves variables using the real registry`() = runTest {
        // Given a payload where LEN=3 (big-endian, single byte at index 0), and a data definition
        // whose end depends on LEN.value
        val payload = HexString("031A2B3CFF")
        val stateWithInput = reducer(createAppState(), AppEvent.CurrentTabEvent.ChangeInputDataEvent(payload))

        val lenDef = ByteGroupDefinition(
            name = "LEN",
            startFormula = "0",
            endFormula = "0",
            representation = Representation(
                dataRenderer = DataRenderer.Integer,
                argumentValues = mapOf(EndiannessArgument.key to Endianness.BigEndian.name),
            ),
        )
        val dataDef = ByteGroupDefinition(
            name = "DATA",
            startFormula = "1",
            endFormula = "[[LEN.value]]",
        )
        var state = reducer(stateWithInput, AppEvent.CurrentTabEvent.AddDefinitionEvent(lenDef))
        state = reducer(state, AppEvent.CurrentTabEvent.AddDefinitionEvent(dataDef))

        // When duplicating DATA (real resolved start=1, end=3, length=3)
        val newState = reducer(state, AppEvent.CurrentTabEvent.DuplicateDefinitionEvent(dataDef))

        // Then the duplicate starts right after the real resolved end (index 4)
        val duplicated = newState.tabsState.selectedTab.groupDefinitions
            .first { it.name == "DATA 2" }
        assertEquals("4", duplicated.startFormula)
        assertEquals("6", duplicated.endFormula)
    }

    @Test
    fun `UndoClearAllDefinitionsEvent restores definitions`() = runTest {
        // Given
        val defs = listOf(
            ByteGroupDefinition.createFromRange(0..1, "Test")
        )
        val tab = TabData()
        val state = createAppState(tabs = listOf(tab))
        val rendering = TabDataRendering(groupDefinitions = defs)
        val event = AppEvent.CurrentTabEvent.UndoClearAllDefinitionsEvent(tab.id, rendering, null)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(defs, newState.tabsState.selectedTab.groupDefinitions)
    }

    @Test
    fun `OpenRenderedByteItemInNewTabEvent opens a new tab with Hexadecimal representation`() = runTest {
        // Given
        val state = createAppState()
        val byteItem = SingleByte(index = 0, value = "AA")
        val representation = Representation(dataRenderer = DataRenderer.Hexadecimal)
        val event = AppEvent.OpenRenderedByteItemInNewTabEvent(byteItem, representation)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(2, newState.tabsState.tabs.size)
        val newTab = newState.tabsState.tabs.last()
        assertEquals(byteItem.name, newTab.name)
        assertEquals(InputType.HEX, newTab.rendering.inputData.inputType)
        assertEquals("AA", newTab.rendering.inputData.hexStringValue)
    }

    @Test
    fun `OpenRenderedByteItemInNewTabEvent opens a new tab with Binary representation`() = runTest {
        // Given
        val state = createAppState()
        val byteItem = SingleByte(index = 0, value = "AA")
        val representation = Representation(dataRenderer = DataRenderer.Binary)
        val event = AppEvent.OpenRenderedByteItemInNewTabEvent(byteItem, representation)

        // When
        val newState = reducer(state, event)

        // Then
        assertEquals(2, newState.tabsState.tabs.size)
        val newTab = newState.tabsState.tabs.last()
        assertEquals(byteItem.name, newTab.name)
        assertEquals(InputType.BINARY, newTab.rendering.inputData.inputType)
        assertEquals("10101010", (newTab.rendering.inputData as fr.nicopico.petitboutiste.models.data.BinaryString).value)
    }

    @Test
    fun `OpenRenderedByteItemInNewTabEvent does nothing for SubTemplate with missing template file`() = runTest {
        // Given
        val state = createAppState()
        val byteItem = SingleByte(index = 0, value = "AA")
        // SubTemplate representation without the required templateFile argument
        // This will cause rendering to fail, so no new tab is created
        val representation = Representation(
            dataRenderer = DataRenderer.SubTemplate,
            argumentValues = mapOf("templateFile" to "nonexistent.json")
        )
        val event = AppEvent.OpenRenderedByteItemInNewTabEvent(byteItem, representation)

        // When
        val newState = reducer(state, event)

        // Then - no new tab is added because rendering fails (template file doesn't exist)
        assertEquals(1, newState.tabsState.tabs.size)
    }

    @Test
    fun `OpenRenderedByteItemInNewTabEvent does nothing for unsupported representation`() = runTest {
        // Given
        val state = createAppState()
        val byteItem = SingleByte(index = 0, value = "AA")
        val representation = Representation(dataRenderer = DataRenderer.Integer)
        val event = AppEvent.OpenRenderedByteItemInNewTabEvent(byteItem, representation)

        // When
        val newState = reducer(state, event)

        // Then - no new tab is added
        assertEquals(1, newState.tabsState.tabs.size)
    }
}
