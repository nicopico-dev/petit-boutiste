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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PTBViewModel(
    private val reducer: Reducer,
    private val appStateRepository: AppStateRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        appStateRepository.restore()
    )
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val eventChannel = Channel<AppEvent>(Channel.BUFFERED)

    private val _snackbarState = MutableStateFlow<SnackbarState?>(null)
    val snackbarState: StateFlow<SnackbarState?> = _snackbarState.asStateFlow()
    private var snackbarDismissJob: Job? = null

    init {
        viewModelScope.launch {
            for (event in eventChannel) {
                try {
                    val previousState = _state.value

                    val newState = reducer(_state.value, event)
                    _state.value = newState

                    val snackbar = event.getEventSnackbar(previousState, ::onAppEvent)
                    if (snackbar != null) {
                        snackbarDismissJob?.cancel()
                        _snackbarState.value = snackbar

                        // Auto-hide snackbar after 5 seconds
                        snackbarDismissJob = launch {
                            delay(5000)
                            _snackbarState.value = null
                        }
                    }
                } catch (error: Exception) {
                    logError("Error processing event: $event\n-> $error")
                }
            }
        }
    }

    val appTheme = _state
        .map { it.appTheme }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _state.value.appTheme,
        )

    val tabsState = _state
        .map { TabsState(it.tabs, it.selectedTabId) }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _state.value.let {
                TabsState(it.tabs, it.selectedTabId)
            },
        )

    val currentTab = _state
        .map { it.selectedTab }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _state.value.selectedTab,
        )

    fun onAppEvent(event: AppEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    fun onAppClose() {
        appStateRepository.save(_state.value)
    }

    fun dismissSnackbar() {
        snackbarDismissJob?.cancel()
        _snackbarState.value = null
    }
}
