package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.styling.default
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.ui.component.styling.DividerStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarVisibility
import org.jetbrains.jewel.ui.component.styling.TrackClickBehavior
import org.jetbrains.jewel.ui.theme.colorPalette
import org.jetbrains.jewel.ui.theme.dividerStyle
import org.jetbrains.jewel.ui.theme.scrollbarStyle
import org.jetbrains.jewel.ui.util.fromArgbHexStringOrNull
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

@Suppress("ClassName")
object JewelThemeUtils {

    private val _darkMode = mutableStateOf(currentSystemTheme == SystemTheme.DARK)
    var darkMode: Boolean
        get() = _darkMode.value
        set(value) {
            _darkMode.value = value
        }

    val dividerStyle: DividerStyle
        @Composable
        get() {
            val dividerStyle = JewelTheme.dividerStyle
            val borderColor = colors.borderColor
            return remember(dividerStyle, borderColor) {
                DividerStyle(
                    color = borderColor,
                    metrics = dividerStyle.metrics,
                )
            }
        }

    val scrollbarStyle: ScrollbarStyle
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

    object colors {
        val titleBarIconTint = hexColor("FFCED0D6")

        val subTextColor: Color
            @Composable
            get() = Color.Gray

        val dangerousActionColor: Color
            @Composable
            get() = if (darkMode) {
                JewelTheme.colorPalette.red[6]
            } else JewelTheme.colorPalette.red[3]

        val errorColor: Color
            @Composable
            get() = if (darkMode) {
                JewelTheme.colorPalette.red[6]
            } else JewelTheme.colorPalette.red[3]

        val accentColor: Color
            @Composable
            get() = if (darkMode) {
                JewelTheme.colorPalette.blue[10]
            } else JewelTheme.colorPalette.blue[0]

        val accentContainer: Color
            @Composable
            get() = if (darkMode) {
                JewelTheme.colorPalette.gray[2]
            } else JewelTheme.colorPalette.blue[10]

        val borderColor: Color
            @Composable
            get() = if (darkMode) {
                JewelTheme.colorPalette.gray[2]
            } else JewelTheme.colorPalette.gray[9]

        val inputBackgroundColor: Color
            @Composable
            get() = JewelTheme.globalColors.panelBackground

        val windowBackgroundColor: Color
            @Composable
            get() = if (darkMode) {
                JewelTheme.colorPalette.gray[0]
            } else JewelTheme.colorPalette.gray[11]
    }

    object typography {
        val title : TextStyle
            @Composable
            get() = JewelTheme.createDefaultTextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = JewelTheme.defaultTextStyle.fontSize * 1.1,
            )

        val data: TextStyle
            @Composable
            get() = JewelTheme.createDefaultTextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = JewelTheme.defaultTextStyle.fontSize * 1.2,
            )
    }

    private fun hexColor(argb: String): Color {
        return Color.fromArgbHexStringOrNull(argb)!!
    }
}
