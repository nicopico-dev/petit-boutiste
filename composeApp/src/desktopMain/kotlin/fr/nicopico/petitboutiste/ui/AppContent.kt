/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.state.SnackbarState
import fr.nicopico.petitboutiste.state.TabData
import fr.nicopico.petitboutiste.ui.components.foundation.PBSnackbar

@Composable
fun AppContent(
    tabData: TabData,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarState? = null,
    onDismissSnackbar: () -> Unit = {},
) {
    var byteItems: List<ByteItem> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(tabData) {
        if (!tabData.isRendered) {
            byteItems = emptyList()
        }
        byteItems = tabData.renderByteItems()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main app screen with the selected tab's data
            TabContent(
                inputData = tabData.inputData,
                definitions = tabData.groupDefinitions,
                byteItems = byteItems,
                scratchpad = tabData.scratchpad,
            )
        }

        if (snackbarState != null) {
            PBSnackbar(
                state = snackbarState,
                onDismiss = { onDismissSnackbar() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
