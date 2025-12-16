/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.styles
import fr.nicopico.petitboutiste.utils.compose.Slot
import org.jetbrains.jewel.ui.component.HorizontalSplitLayout
import org.jetbrains.jewel.ui.component.SplitLayoutState
import org.jetbrains.jewel.ui.component.VerticalSplitLayout
import org.jetbrains.jewel.ui.component.rememberSplitLayoutState

@Composable
fun DesktopScaffold(
    main: Slot,
    side: Slot,
    tools: Slot? = null,
) {
    val dividerStyle = AppTheme.current.styles.dividerStyle

    HorizontalSplitLayout(
        first = {
            val verticalSplitLayoutState = remember(tools != null) {
                SplitLayoutState(
                    initialSplitFraction = if (tools != null) 0.67f else 1f
                )
            }

            VerticalSplitLayout(
                first = main,
                second = tools ?: {},
                state = verticalSplitLayoutState,
                dividerStyle = dividerStyle,
                draggableWidth = 16.dp,
                firstPaneMinWidth = 200.dp,
                secondPaneMinWidth = if (tools != null) 100.dp else 0.dp,
            )
        },
        second = side,
        state = rememberSplitLayoutState(0.70f),
        dividerStyle = dividerStyle,
        draggableWidth = 16.dp,
        firstPaneMinWidth = 300.dp,
        secondPaneMinWidth = 250.dp,
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, color = dividerStyle.color),
    )
}
