/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.nicopico.petitboutiste.models.state.AppState
import fr.nicopico.petitboutiste.models.state.events.AppEvent
import fr.nicopico.petitboutiste.models.state.events.SnackbarEvent
import fr.nicopico.petitboutiste.models.state.events.getSnackbarEvent
import fr.nicopico.petitboutiste.repository.AppStateRepository
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
import kotlin.time.Duration.Companion.milliseconds

class PTBViewModel(
    private val reducer: Reducer,
    private val appStateRepository: AppStateRepository,
) : ViewModel() {

    val state: StateFlow<AppState>
        field = MutableStateFlow(appStateRepository.restore())

    val snackbarEvent: StateFlow<SnackbarEvent?>
        field = MutableStateFlow<SnackbarEvent?>(null)

    private val eventChannel = Channel<AppEvent>(Channel.BUFFERED)
    private var snackbarDismissJob: Job? = null

    init {
        viewModelScope.launch {
            processEvent(AppEvent.RefreshRenderingEvent)
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
        .map { it.tabsState }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = state.value.tabsState,
        )

    val currentTab = state
        .map { it.tabsState.selectedTab }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = state.value.tabsState.selectedTab,
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

        val snackbar = event.getSnackbarEvent(previousState, ::onAppEvent)
        if (snackbar != null) {
            displaySnackBar(snackbar)
        }
    }

    fun onAppClose() {
        appStateRepository.save(state.value)
    }

    fun displaySnackBar(snackbar: SnackbarEvent) {
        snackbarDismissJob?.cancel()
        snackbarEvent.value = snackbar

        // Auto-hide snackbar after 5 seconds
        snackbarDismissJob = viewModelScope.launch {
            delay(5000.milliseconds)
            snackbarEvent.value = null
        }
    }

    fun dismissSnackbar() {
        snackbarDismissJob?.cancel()
        snackbarEvent.value = null
    }
}
