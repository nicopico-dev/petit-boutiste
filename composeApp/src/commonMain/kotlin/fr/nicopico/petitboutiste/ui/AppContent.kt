/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.ui.components.TabContent
import fr.nicopico.petitboutiste.utils.preview.WrapForPreviewDesktop

@TraceRecomposition
@Composable
fun AppContent(
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    val selectedTab by remember(appState) {
        derivedStateOf {
            appState.tabs.first {
                it.id == appState.selectedTabId
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Main app screen with the selected tab's data
        TabContent(
            inputData = selectedTab.inputData,
            definitions = selectedTab.groupDefinitions,
            inputType = selectedTab.inputType,
            scratchpad = selectedTab.scratchpad,
        )
    }
}

@Preview
@Composable
private fun AppContentPreview() {
    WrapForPreviewDesktop {
        AppContent(
            AppState(),
            modifier = Modifier.size(
                width = 400.dp,
                height = 300.dp
            )
        )
    }
}
