package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
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
import org.jetbrains.jewel.ui.theme.dividerStyle
import org.jetbrains.jewel.ui.theme.scrollbarStyle
import org.jetbrains.jewel.ui.util.fromArgbHexStringOrNull
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

@Suppress("ClassName")
object JewelThemeUtils {

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

    private val isDarkTheme = currentSystemTheme == SystemTheme.DARK

    object colors {
        val iconOnLightTint = hexColor("FF6C707E")
        val iconOnDarkTint = hexColor("FFCED0D6")

        val iconTint: Color
            get() = if (isDarkTheme) iconOnDarkTint else iconOnLightTint

        // TODO Use JewelTheme.colorPalette instead
        val subTextColor = Color.Gray
        val dangerousActionColor = Color.Red
        val errorColor = Color.Red

        val accentColor: Color = hexColor("FF1F3D91")
        val accentContainer: Color = hexColor("FFDEE4F8")

        // TODO Use JewelTheme.globalColors.border.normal
        val borderColor: Color
            @Composable
            get() = Color.LightGray

        val inputBackgroundColor: Color
            @Composable
            get() = JewelTheme.globalColors.panelBackground
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
