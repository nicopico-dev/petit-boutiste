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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
    val byteItems = tabData.renderByteItems()
    val errors = tabData.rendering.errors

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main app screen with the selected tab's data
            TabContent(
                inputData = tabData.inputData,
                definitions = tabData.groupDefinitions,
                byteItems = byteItems,
                errors = errors,
                scratchpad = tabData.scratchpad,
            )
        }

        if (snackbarState != null) {
            PBSnackbar(
                state = snackbarState,
                onDismiss = { onDismissSnackbar() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .testTag(UiTags.SNACKBAR)
            )
        }
    }
}
