/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.nicopico.petitboutiste.models.persistence.getScreenCharacteristics
import fr.nicopico.petitboutiste.repository.AppStateRepository
import fr.nicopico.petitboutiste.repository.TemplateManager
import fr.nicopico.petitboutiste.repository.WindowStateRepository
import fr.nicopico.petitboutiste.state.OnAppEvent
import fr.nicopico.petitboutiste.state.OnSnackbar
import fr.nicopico.petitboutiste.state.Reducer
import fr.nicopico.petitboutiste.ui.AppContent
import fr.nicopico.petitboutiste.ui.AppShortcuts
import fr.nicopico.petitboutiste.ui.components.foundation.PBMenuBar
import fr.nicopico.petitboutiste.ui.components.foundation.PBTitleBar
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.PBIcons
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.ui.theme.invoke
import org.jetbrains.jewel.window.DecoratedWindow

private val windowStateRepository = WindowStateRepository()

val LocalOnAppEvent = staticCompositionLocalOf<OnAppEvent> {
    { /* no-op */ }
}
val LocalOnSnackbar = staticCompositionLocalOf<OnSnackbar> {
    { /* no-op */ }
}

@Composable
fun PetitBoutiste(
    onAppClose: () -> Unit,
    viewModel: PTBViewModel = viewModel {
        PTBViewModel(
            reducer = Reducer(
                templateManager = TemplateManager(),
            ),
            appStateRepository = AppStateRepository(),
        )
    }
) {
    val screenCharacteristics = getScreenCharacteristics()
    val windowState = rememberSaveable {
        windowStateRepository.restore(screenCharacteristics) ?: WindowState()
    }

    val appTheme by viewModel.appTheme
        .collectAsStateWithLifecycle()
    val tabsState by viewModel.tabsState
        .collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab
        .collectAsStateWithLifecycle()
    val snackbarState by viewModel.snackbarState
        .collectAsStateWithLifecycle()

    appTheme {
        DecoratedWindow(
            title = "Petit Boutiste",
            icon = PBIcons.app,
            onCloseRequest = {
                windowStateRepository.save(windowState, screenCharacteristics)
                viewModel.onAppClose()
                onAppClose()
            },
            state = windowState,
            content = {
                CompositionLocalProvider(
                    LocalOnAppEvent provides { event ->
                        viewModel.onAppEvent(event)
                    },
                    LocalOnSnackbar provides { snackbar ->
                        viewModel.displaySnackBar(snackbar)
                    },
                ) {
                    PBMenuBar(tabsState)
                    PBTitleBar(
                        tabsState = tabsState,
                        appTheme = appTheme,
                    )
                    AppShortcuts {
                        AppContent(
                            tabData = currentTab,
                            snackbarState = snackbarState,
                            modifier = Modifier
                                .background(AppTheme.current.colors.windowBackgroundColor),
                            onDismissSnackbar = viewModel::dismissSnackbar,
                        )
                    }
                }
            }
        )
    }
}
