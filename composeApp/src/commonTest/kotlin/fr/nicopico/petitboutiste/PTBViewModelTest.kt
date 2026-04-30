/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.repository.AppStateRepository
import fr.nicopico.petitboutiste.repository.TemplateManager
import fr.nicopico.petitboutiste.state.AppEvent
import fr.nicopico.petitboutiste.state.AppState
import fr.nicopico.petitboutiste.state.Reducer
import fr.nicopico.petitboutiste.state.SnackbarState
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
import kotlinx.io.files.Path
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
        reducer = Reducer(templateManager)
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
        val snackbar = SnackbarState(message = "Test message")

        // When
        viewModel.displaySnackBar(snackbar)

        // Then
        assertEquals(snackbar, viewModel.snackbarState.value)
    }

    @Test
    fun `dismissSnackbar clears snackbarState`() = runTest {
        // Given
        createViewModel()
        val snackbar = SnackbarState(message = "Test message")
        viewModel.displaySnackBar(snackbar)

        // When
        viewModel.dismissSnackbar()

        // Then
        assertNull(viewModel.snackbarState.value)
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
        assertNotNull(viewModel.snackbarState.value)
        assertEquals("All definitions cleared", viewModel.snackbarState.value?.message)

        // Advance time to trigger auto-hide
        advanceTimeBy(5000.milliseconds)
        runCurrent()
        assertNull(viewModel.snackbarState.value)
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
        assertEquals(viewModel.state.value.selectedTabId, viewModel.tabsState.value.selectedTabId)
        assertEquals(viewModel.state.value.selectedTabId, viewModel.currentTab.value.id)
    }

    private class FakeAppStateRepository : AppStateRepository {
        var savedState: AppState = AppState()
        override fun save(appState: AppState) {
            savedState = appState
        }
        override fun restore(): AppState = savedState
    }

    private class FakeTemplateManager : TemplateManager {
        override suspend fun loadTemplate(templateFilePath: Path): Template = Template(name = "Default")
        override suspend fun saveTemplate(template: Template, templateFilePath: Path, overwrite: Boolean) {}
    }
}
