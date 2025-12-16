/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle

val PBTheme.typography: PBThemeTypography
    @Composable
    get() = remember(this) {
        PBThemeTypographyImpl()
    }

interface PBThemeTypography {
    val title: TextStyle
        @Composable get
    val data: TextStyle
        @Composable get
}

private class PBThemeTypographyImpl : PBThemeTypography {

    override val title : TextStyle
        @Composable
        get() {
            val defaultFontSize = JewelTheme.defaultTextStyle.fontSize
            return remember(this, defaultFontSize) {
                JewelTheme.createDefaultTextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = defaultFontSize * 1.1,
                )
            }
        }

    override val data: TextStyle
        @Composable
        get() {
            val defaultFontSize = JewelTheme.defaultTextStyle.fontSize
            return remember(this, defaultFontSize) {
                JewelTheme.createDefaultTextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = defaultFontSize * 1.2,
                )
            }
        }
}
