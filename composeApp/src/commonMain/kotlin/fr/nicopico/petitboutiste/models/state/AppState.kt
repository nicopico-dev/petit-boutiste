/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.state

import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.ui.theme.PBTheme

data class AppState(
    val tabsState: TabsState = TabsState(
        tabs = listOf(
            TabData(
                rendering = TabDataRendering(
                    inputData = HexString("FF00"),
                )
            )
        ),
    ),
    val appTheme: PBTheme = PBTheme.System,
)
