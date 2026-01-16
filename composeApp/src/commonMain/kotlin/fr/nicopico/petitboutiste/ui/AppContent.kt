/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.nicopico.petitboutiste.state.TabData

@Composable
fun AppContent(
    tabData: TabData,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Main app screen with the selected tab's data
        TabContent(
            inputData = tabData.inputData,
            definitions = tabData.groupDefinitions,
            scratchpad = tabData.scratchpad,
        )
    }
}
