/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.util.fromArgbHexStringOrNull

val PBTheme.colors: PBThemeColors
    @Composable
    get() = remember(this) {
        PBThemeColorsImpl(this)
    }

interface PBThemeColors {
    val titleBarIconTint: Color
        @Composable get
    val subTextColor: Color
        @Composable get
    val dangerousActionColor: Color
        @Composable get
    val errorColor: Color
        @Composable get
    val accentColor: Color
        @Composable get
    val accentContainer: Color
        @Composable get
    val borderColor: Color
        @Composable get
    val windowBackgroundColor: Color
        @Composable get
}

// TODO Add smooth transitions between light and dark themes
//  https://michaelevans.org/blog/2025/07/01/smooth-theme-transitions-in-compose-with-animated-colorschemes/
private class PBThemeColorsImpl(
    private val theme: PBTheme,
) : PBThemeColors {

    override val titleBarIconTint: Color
        @Composable
        get() = Color.fromArgbHexStringOrNull("FFCED0D6")
            ?: Color.Unspecified

    override val subTextColor: Color
        @Composable
        get() = Color.Gray

    override val dangerousActionColor: Color
        @Composable
        get() = if (theme.isDark) {
            JewelTheme.colorPalette.red[6]
        } else JewelTheme.colorPalette.red[3]

    override val errorColor: Color
        @Composable
        get() = if (theme.isDark) {
            JewelTheme.colorPalette.red[6]
        } else JewelTheme.colorPalette.red[3]

    override val accentColor: Color
        @Composable
        get() = if (theme.isDark) {
            JewelTheme.colorPalette.blue[10]
        } else JewelTheme.colorPalette.blue[0]

    override val accentContainer: Color
        @Composable
        get() = if (theme.isDark) {
            JewelTheme.colorPalette.gray[2]
        } else JewelTheme.colorPalette.blue[10]

    override val borderColor: Color
        @Composable
        get() = if (theme.isDark) {
            JewelTheme.colorPalette.gray[2]
        } else JewelTheme.colorPalette.gray[9]

    override val windowBackgroundColor: Color
        @Composable
        get() = if (theme.isDark) {
            JewelTheme.colorPalette.gray[0]
        } else JewelTheme.colorPalette.gray[11]
}
