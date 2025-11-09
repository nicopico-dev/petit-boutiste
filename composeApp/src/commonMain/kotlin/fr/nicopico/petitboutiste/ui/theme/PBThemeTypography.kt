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
        get() = JewelTheme.createDefaultTextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = JewelTheme.defaultTextStyle.fontSize * 1.1,
        )

    override val data: TextStyle
        @Composable
        get() = JewelTheme.createDefaultTextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = JewelTheme.defaultTextStyle.fontSize * 1.2,
        )
}
