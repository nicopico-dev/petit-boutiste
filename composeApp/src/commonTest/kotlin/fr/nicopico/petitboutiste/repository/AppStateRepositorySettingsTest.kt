/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import com.russhwolf.settings.MapSettings
import fr.nicopico.petitboutiste.models.data.BinaryString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.state.AppState
import fr.nicopico.petitboutiste.state.TabData
import fr.nicopico.petitboutiste.state.TabDataRendering
import fr.nicopico.petitboutiste.state.TabId
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppStateRepositorySettingsTest {

    private val settings = MapSettings()
    private val repository = AppStateRepositorySettings(settings)

    @Test
    fun `saves and restores AppState`() {
        // Given
        val tab1 = TabData(
            id = TabId("tab1"),
            name = "Hex Tab",
            rendering = TabDataRendering(
                inputData = HexString("AABBCC"),
                groupDefinitions = emptyList()
            ),
            scratchpad = "some notes"
        )
        val tab2 = TabData(
            id = TabId("tab2"),
            name = "Binary Tab",
            rendering = TabDataRendering(
                inputData = BinaryString("10101010"),
                groupDefinitions = emptyList()
            )
        )
        val appState = AppState(
            tabs = listOf(tab1, tab2),
            selectedTabId = tab2.id,
            appTheme = PBTheme.Dark
        )

        // When
        repository.save(appState)
        val restored = repository.restore()

        // Then
        assertEquals(appState.selectedTabId, restored.selectedTabId)
        assertEquals(appState.appTheme, restored.appTheme)
        assertEquals(appState.tabs.size, restored.tabs.size)
        
        val restoredTab1 = restored.tabs.first { it.id == tab1.id }
        assertEquals(tab1.name, restoredTab1.name)
        assertEquals(tab1.inputData.hexString, restoredTab1.inputData.hexString)
        assertEquals(tab1.scratchpad, restoredTab1.scratchpad)

        val restoredTab2 = restored.tabs.first { it.id == tab2.id }
        assertEquals(tab2.name, restoredTab2.name)
        assertEquals(tab2.inputData.hexString, restoredTab2.inputData.hexString)
        // Check input type indirectly
        assertTrue(restoredTab2.inputData is BinaryString)
    }

    @Test
    fun `restore returns default AppState when no data is persisted`() {
        // When
        val restored = repository.restore()

        // Then
        assertEquals(1, restored.tabs.size)
        assertEquals(PBTheme.System, restored.appTheme)
        assertEquals(restored.tabs.first().id, restored.selectedTabId)
    }

    @Test
    fun `restore handles invalid app theme by falling back to System`() {
        // Given
        settings.putString("APP_STATE", """{"tabs":[],"selectedTabId":"tab1","appTheme":"INVALID_THEME"}""")

        // When
        val restored = repository.restore()

        // Then
        assertEquals(PBTheme.System, restored.appTheme)
    }
}
