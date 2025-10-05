package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.styling.TitleBarStyle
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

@Composable
fun PetitBoutisteTheme(content: @Composable () -> Unit) {
    // TODO Follow system theme changes
    val isDark = currentSystemTheme == SystemTheme.DARK

    IntUiTheme(
        theme = if (isDark) JewelTheme.darkThemeDefinition() else JewelTheme.lightThemeDefinition(),
        styling = ComponentStyling.default()
            .decoratedWindow(
                titleBarStyle = if (isDark) TitleBarStyle.dark() else TitleBarStyle.light(),
            )
    ) {
        content()
    }
}
