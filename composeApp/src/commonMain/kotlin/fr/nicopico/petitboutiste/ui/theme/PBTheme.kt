package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import fr.nicopico.petitboutiste.ui.theme.system.observeSystemTheme
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle
import org.jetbrains.skiko.SystemTheme

enum class PBTheme {
    System,
    Light,
    Dark,
}

val PBTheme.isDark: Boolean
    @Composable
    get() {
        val systemTheme by observeSystemTheme()
        return remember(this, systemTheme) {
            this == PBTheme.Dark
                || (this == PBTheme.System && systemTheme == SystemTheme.DARK)
        }
    }

val AppTheme = compositionLocalOf { PBTheme.System }

@Composable
operator fun PBTheme.invoke(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        AppTheme provides this
    ) {
        IntUiTheme(
            theme = if (isDark) JewelTheme.darkThemeDefinition() else JewelTheme.lightThemeDefinition(),
            styling = ComponentStyling.default()
                .decoratedWindow(
                    titleBarStyle = if (isDark) TitleBarStyle.dark() else TitleBarStyle.light(),
                    windowStyle = if (isDark) DecoratedWindowStyle.dark() else DecoratedWindowStyle.light(),
                ),
            content = content,
        )
    }
}
