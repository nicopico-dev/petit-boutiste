/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.default
import org.jetbrains.jewel.ui.component.styling.DividerStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarVisibility
import org.jetbrains.jewel.ui.component.styling.TrackClickBehavior
import org.jetbrains.jewel.ui.theme.dividerStyle
import org.jetbrains.jewel.ui.theme.scrollbarStyle

val PBTheme.styles: PBThemeStyles
    @Composable
    get() = remember(this) {
        PBThemeStylesImpl(this)
    }

interface PBThemeStyles {
    val dividerStyle: DividerStyle
        @Composable get

    val scrollbarStyle: ScrollbarStyle
        @Composable get
}

private class PBThemeStylesImpl(
    private val theme: PBTheme,
) : PBThemeStyles {
    override val dividerStyle: DividerStyle
        @Composable
        get() {
            val dividerStyle = JewelTheme.dividerStyle
            val borderColor = theme.colors.borderColor
            return remember(dividerStyle, borderColor) {
                DividerStyle(
                    color = borderColor,
                    metrics = dividerStyle.metrics,
                )
            }
        }

    override val scrollbarStyle: ScrollbarStyle
        @Composable
        get() {
            val scrollbarStyle = JewelTheme.scrollbarStyle
            return remember(scrollbarStyle) {
                ScrollbarStyle(
                    colors = scrollbarStyle.colors,
                    metrics = scrollbarStyle.metrics,
                    trackClickBehavior = TrackClickBehavior.JumpToSpot,
                    scrollbarVisibility = ScrollbarVisibility.WhenScrolling.default()
                )
            }
        }

}
