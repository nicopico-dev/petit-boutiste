/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import fr.nicopico.petitboutiste.fakes.FakeAppStateRepository
import fr.nicopico.petitboutiste.fakes.FakeTemplateManager
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.SingleByte
import fr.nicopico.petitboutiste.models.state.AppState
import fr.nicopico.petitboutiste.models.state.TabData
import fr.nicopico.petitboutiste.models.state.TabDataRendering
import fr.nicopico.petitboutiste.models.state.TabsState
import fr.nicopico.petitboutiste.models.state.events.AppEvent
import fr.nicopico.petitboutiste.models.state.events.SnackbarEvent
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class PTBViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var appStateRepository: FakeAppStateRepository
    private lateinit var templateManager: FakeTemplateManager
    private lateinit var reducer: Reducer
    private lateinit var viewModel: PTBViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        appStateRepository = FakeAppStateRepository()
        templateManager = FakeTemplateManager()
        reducer = Reducer(templateManager, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = PTBViewModel(reducer, appStateRepository)
    }

    @Test
    fun `initial state is restored from repository`() = runTest {
        // Given
        val restoredState = AppState(appTheme = PBTheme.Dark)
        appStateRepository.savedState = restoredState

        // When
        createViewModel()

        // Then
        assertEquals(restoredState, viewModel.state.value)
    }

    @Test
    fun `onAppEvent processes event via reducer`() = runTest {
        // Given
        createViewModel()
        val event = AppEvent.SwitchAppThemeEvent(PBTheme.Light)

        // When
        viewModel.onAppEvent(event)
        advanceUntilIdle()

        // Then
        assertEquals(PBTheme.Light, viewModel.state.value.appTheme)
        assertEquals(PBTheme.Light, viewModel.appTheme.value)
    }

    @Test
    fun `onAppClose saves state to repository`() = runTest {
        // Given
        createViewModel()
        val currentState = viewModel.state.value

        // When
        viewModel.onAppClose()

        // Then
        assertEquals(currentState, appStateRepository.savedState)
    }

    @Test
    fun `displaySnackBar updates snackbarState`() = runTest {
        // Given
        createViewModel()
        val snackbar = SnackbarEvent(message = "Test message")

        // When
        viewModel.displaySnackbar(snackbar)

        // Then
        assertEquals(snackbar, viewModel.snackbarEvent.value)
    }

    @Test
    fun `dismissSnackbar clears snackbarState`() = runTest {
        // Given
        createViewModel()
        val snackbar = SnackbarEvent(message = "Test message")
        viewModel.displaySnackbar(snackbar)

        // When
        viewModel.dismissSnackbar()

        // Then
        assertNull(viewModel.snackbarEvent.value)
    }

    @Test
    fun `events that return a snackbar are displayed`() = runTest {
        // Given
        createViewModel()
        // AppEvent.CurrentTabEvent.ClearAllDefinitionsEvent triggers a snackbar
        val event = AppEvent.CurrentTabEvent.ClearAllDefinitionsEvent

        // When
        viewModel.onAppEvent(event)
        // Process the event but don't reach the 5s delay
        advanceTimeBy(100.milliseconds)
        runCurrent()

        // Then
        assertNotNull(viewModel.snackbarEvent.value)
        assertEquals("All definitions cleared", viewModel.snackbarEvent.value?.message)

        // Advance time to trigger auto-hide
        advanceTimeBy(5000.milliseconds)
        runCurrent()
        assertNull(viewModel.snackbarEvent.value)
    }

    @Test
    fun `derived flows update when state changes`() = runTest {
        // Given
        createViewModel()

        // When - Change theme
        viewModel.onAppEvent(AppEvent.SwitchAppThemeEvent(PBTheme.Dark))
        advanceUntilIdle()
        // Then
        assertEquals(PBTheme.Dark, viewModel.appTheme.value)

        // When - Add tab
        val initialTabsCount = viewModel.tabsState.value.tabs.size
        viewModel.onAppEvent(AppEvent.AddNewTabEvent())
        advanceUntilIdle()
        // Then
        assertEquals(initialTabsCount + 1, viewModel.tabsState.value.tabs.size)
        assertEquals(viewModel.state.value.tabsState.selectedTabId, viewModel.tabsState.value.selectedTabId)
        assertEquals(viewModel.state.value.tabsState.selectedTabId, viewModel.currentTab.value.id)
    }

    @Test
    fun `restored state has rendered byte items`() = runTest {
        // Given
        val inputData = HexString("AABBCC")
        val tab = TabData(
            rendering = TabDataRendering(
                inputData = inputData,
            )
        )
        val restoredState = AppState(
            tabsState = TabsState(
                tabs = listOf(tab),
                selectedTabId = tab.id,
            )
        )
        appStateRepository.savedState = restoredState

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val renderedItems = viewModel.currentTab.value.rendering.byteItems
        assertEquals(3, renderedItems.size, "Should have 3 single bytes rendered")
        assertEquals("AA", (renderedItems[0] as SingleByte).value)
    }
}
