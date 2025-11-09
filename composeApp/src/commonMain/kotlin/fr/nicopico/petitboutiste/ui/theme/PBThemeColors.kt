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
