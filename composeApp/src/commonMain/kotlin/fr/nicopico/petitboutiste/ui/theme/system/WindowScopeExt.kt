package fr.nicopico.petitboutiste.ui.theme.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.window.DialogWindowScope
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.ui.theme.isDark
import java.awt.Color as AwtColor

@Composable
fun DialogWindowScope.adaptWindowAppearance() {
    val appTheme = AppTheme.current
    val isDark = appTheme.isDark
    val windowBackgroundColor = AwtColor(
        appTheme.colors.windowBackgroundColor.toArgb(),
        true,
    )

    LaunchedEffect(isDark) {
        window.rootPane.putClientProperty(
            "apple.awt.windowAppearance",
            if (isDark) "NSAppearanceNameVibrantDark"
            else "NSAppearanceNameLight",
        )
        window.background = windowBackgroundColor
    }
}
