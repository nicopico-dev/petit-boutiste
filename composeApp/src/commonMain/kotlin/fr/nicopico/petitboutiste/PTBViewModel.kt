/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.nicopico.petitboutiste.repository.AppStateRepository
import fr.nicopico.petitboutiste.state.AppEvent
import fr.nicopico.petitboutiste.state.AppState
import fr.nicopico.petitboutiste.state.Reducer
import fr.nicopico.petitboutiste.state.SnackbarState
import fr.nicopico.petitboutiste.state.TabsState
import fr.nicopico.petitboutiste.state.getEventSnackbar
import fr.nicopico.petitboutiste.state.selectedTab
import fr.nicopico.petitboutiste.utils.logError
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PTBViewModel(
    private val reducer: Reducer,
    private val appStateRepository: AppStateRepository,
) : ViewModel() {

    val state: StateFlow<AppState>
        field = MutableStateFlow(appStateRepository.restore())

    val snackbarState: StateFlow<SnackbarState?>
        field = MutableStateFlow<SnackbarState?>(null)

    private val eventChannel = Channel<AppEvent>(Channel.BUFFERED)
    private var snackbarDismissJob: Job? = null

    init {
        viewModelScope.launch {
            for (event in eventChannel) {
                processEvent(event)
            }
        }
    }

    val appTheme = state
        .map { it.appTheme }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = state.value.appTheme,
        )

    val tabsState = state
        .map { TabsState(it.tabs, it.selectedTabId) }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = state.value.let {
                TabsState(it.tabs, it.selectedTabId)
            },
        )

    val currentTab = state
        .map { it.selectedTab }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = state.value.selectedTab,
        )

    fun onAppEvent(event: AppEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private suspend fun processEvent(event: AppEvent) {
        val previousState = state.value
        val newState = try {
            reducer(state.value, event)
        } catch (error: Exception) {
            logError("Error processing event: $event", error)
            return // early exit
        }

        state.value = newState

        val snackbar = event.getEventSnackbar(previousState, ::onAppEvent)
        if (snackbar != null) {
            displaySnackBar(snackbar)
        }
    }

    fun onAppClose() {
        appStateRepository.save(state.value)
    }

    fun displaySnackBar(snackbar: SnackbarState) {
        snackbarDismissJob?.cancel()
        snackbarState.value = snackbar

        // Auto-hide snackbar after 5 seconds
        snackbarDismissJob = viewModelScope.launch {
            delay(5000)
            snackbarState.value = null
        }
    }

    fun dismissSnackbar() {
        snackbarDismissJob?.cancel()
        snackbarState.value = null
    }
}
