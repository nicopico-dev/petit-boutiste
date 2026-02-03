/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import fr.nicopico.petitboutiste.state.AppEvent
import fr.nicopico.petitboutiste.state.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.state.TabData
import fr.nicopico.petitboutiste.state.TabId
import fr.nicopico.petitboutiste.state.TabTemplateData
import fr.nicopico.petitboutiste.state.TabsState
import fr.nicopico.petitboutiste.utils.file.FileDialog
import fr.nicopico.petitboutiste.utils.file.FileDialogOperation
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuActionsTest {

    private val capturedEvents = mutableListOf<AppEvent>()
    private val onEvent: (AppEvent) -> Unit = { capturedEvents.add(it) }
    private val testScope = TestScope()
    private val mockFileDialogProvider = object : FileDialog {
        var fileToReturn: File? = null
        var capturedOperation: FileDialogOperation? = null

        override suspend fun show(
            operation: FileDialogOperation,
            title: String?,
            block: (File) -> Unit
        ) {
            capturedOperation = operation
            fileToReturn?.let { block(it) }
        }
    }
    private val defaultTab = TabData()
    private val tabsState = TabsState(tabs = listOf(defaultTab), selectedTabId = defaultTab.id)
    private val menuActions = MenuActions(onEvent, testScope, tabsState, mockFileDialogProvider)

    @Test
    fun `addNewTab triggers AddNewTabEvent`() {
        // When
        menuActions.addNewTab()

        // Then
        assertEquals(1, capturedEvents.size)
        assertTrue(capturedEvents[0] is AppEvent.AddNewTabEvent)
    }

    @Test
    fun `duplicateTab triggers DuplicateTabEvent`() {
        // Given
        val tabId = TabId("test-tab-id")

        // When
        menuActions.duplicateTab(tabId)

        // Then
        assertEquals(1, capturedEvents.size)
        assertEquals(AppEvent.DuplicateTabEvent(tabId), capturedEvents[0])
    }

    @Test
    fun `removeTab triggers RemoveTabEvent and ShowSnackbarEvent`() {
        // Given
        val tabId = defaultTab.id

        // When
        menuActions.removeTab(tabId)

        // Then
        assertEquals(2, capturedEvents.size)
        assertEquals(AppEvent.RemoveTabEvent(tabId), capturedEvents[0])
        assertTrue(capturedEvents[1] is AppEvent.ShowSnackbarEvent)
        val snackbarEvent = capturedEvents[1] as AppEvent.ShowSnackbarEvent
        assertEquals("Undo", snackbarEvent.actionLabel)
    }

    @Test
    fun `saveTemplate triggers SaveTemplateEvent when templateData is present`() {
        // Given
        val file = File("test.json")
        val tabData = TabData(
            templateData = TabTemplateData(templateFile = file)
        )

        // When
        menuActions.saveTemplate(tabData)

        // Then
        assertEquals(1, capturedEvents.size)
        assertEquals(
            CurrentTabEvent.SaveTemplateEvent(file, updateExisting = true),
            capturedEvents[0]
        )
    }

    @Test
    fun `restoreDefinitions triggers LoadTemplateEvent when templateData is present`() {
        // Given
        val file = File("test.json")
        val tabData = TabData(
            templateData = TabTemplateData(templateFile = file)
        )

        // When
        menuActions.restoreDefinitions(tabData)

        // Then
        assertEquals(1, capturedEvents.size)
        assertEquals(
            CurrentTabEvent.LoadTemplateEvent(file, definitionsOnly = true),
            capturedEvents[0]
        )
    }

    @Test
    fun `restoreDefinitions does nothing when templateData is null`() {
        // Given
        val tabData = TabData(templateData = null)

        // When
        menuActions.restoreDefinitions(tabData)

        // Then
        assertTrue(capturedEvents.isEmpty())
    }

    @Test
    fun `clearAllDefinitions triggers ClearAllDefinitionsEvent and ShowSnackbarEvent`() {
        // When
        menuActions.clearAllDefinitions()

        // Then
        assertEquals(2, capturedEvents.size)
        assertEquals(CurrentTabEvent.ClearAllDefinitionsEvent, capturedEvents[0])
        assertTrue(capturedEvents[1] is AppEvent.ShowSnackbarEvent)
        val snackbarEvent = capturedEvents[1] as AppEvent.ShowSnackbarEvent
        assertEquals("Undo", snackbarEvent.actionLabel)
    }

    @Test
    fun `loadTemplate triggers LoadTemplateEvent after file selection`() = runTest {
        // Given
        val file = File("selected.json")
        mockFileDialogProvider.fileToReturn = file

        // When
        menuActions.loadTemplate()
        testScope.testScheduler.runCurrent()

        // Then
        assertEquals(FileDialogOperation.ChooseFile(setOf("json")), mockFileDialogProvider.capturedOperation)
        assertEquals(1, capturedEvents.size)
        assertEquals(
            CurrentTabEvent.LoadTemplateEvent(file, definitionsOnly = false),
            capturedEvents[0]
        )
    }

    @Test
    fun `saveTemplateAs triggers SaveTemplateEvent after file selection`() = runTest {
        // Given
        val file = File("new_template.json")
        mockFileDialogProvider.fileToReturn = file
        val tabData = TabData(name = "My Template")

        // When
        menuActions.saveTemplateAs(tabData)
        testScope.testScheduler.runCurrent()

        // Then
        assertTrue(mockFileDialogProvider.capturedOperation is FileDialogOperation.CreateNewFile)
        val op = mockFileDialogProvider.capturedOperation as FileDialogOperation.CreateNewFile
        assertEquals("My Template", op.suggestedFilename)
        assertEquals("json", op.extension)

        assertEquals(1, capturedEvents.size)
        assertEquals(
            CurrentTabEvent.SaveTemplateEvent(file, updateExisting = false),
            capturedEvents[0]
        )
    }

    @Test
    fun `addDefinitionsFromAnotherTemplate triggers AddDefinitionsFromTemplateEvent after file selection`() = runTest {
        // Given
        val file = File("other.json")
        mockFileDialogProvider.fileToReturn = file

        // When
        menuActions.addDefinitionsFromAnotherTemplate()
        testScope.testScheduler.runCurrent()

        // Then
        assertEquals(FileDialogOperation.ChooseFile(setOf("json")), mockFileDialogProvider.capturedOperation)
        assertEquals(1, capturedEvents.size)
        assertEquals(
            CurrentTabEvent.AddDefinitionsFromTemplateEvent(file),
            capturedEvents[0]
        )
    }
}
