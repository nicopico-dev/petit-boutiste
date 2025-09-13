package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.ui.component.styling.DividerStyle
import org.jetbrains.jewel.ui.theme.dividerStyle
import org.jetbrains.jewel.ui.util.fromArgbHexStringOrNull
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

@Suppress("ClassName")
object JewelThemeUtils {

    @Deprecated("Use colors object", ReplaceWith("JewelThemeUtils.colors.iconOnLightTint"))
    val iconOnLightTint = colors.iconOnLightTint
    @Deprecated("Use colors object", ReplaceWith("JewelThemeUtils.colors.iconOnDarkTint"))
    val iconOnDarkTint = colors.iconOnDarkTint

    @Deprecated("Use colors object", ReplaceWith("JewelThemeUtils.colors.subTextColor"))
    val subTextColor = colors.subTextColor
    @Deprecated("Use colors object", ReplaceWith("JewelThemeUtils.colors.dangerousActionColor"))
    val dangerousActionColor = colors.dangerousActionColor

    val dividerStyle: DividerStyle
        @Composable
        get() = DividerStyle(
            color = Color.LightGray,
            metrics = JewelTheme.dividerStyle.metrics,
        )

    private val isDarkTheme = currentSystemTheme == SystemTheme.DARK

    object colors {
        val iconOnLightTint = hexColor("FF6C707E")
        val iconOnDarkTint = hexColor("FFCED0D6")

        val iconTint: Color
            get() = if (isDarkTheme) iconOnDarkTint else iconOnLightTint

        val subTextColor = Color.Gray
        val dangerousActionColor = Color.Red
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
