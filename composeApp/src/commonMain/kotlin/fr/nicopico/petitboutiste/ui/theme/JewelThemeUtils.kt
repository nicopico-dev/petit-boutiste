package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import org.jetbrains.jewel.ui.component.styling.DividerStyle
import org.jetbrains.jewel.ui.component.styling.ScrollbarStyle

@Deprecated("Use PBTheme extensions")
@Suppress("ClassName")
object JewelThemeUtils {

    var appTheme by mutableStateOf(PBTheme.System)

    val dividerStyle: DividerStyle
        @Composable
        get() = appTheme.styles.dividerStyle

    val scrollbarStyle: ScrollbarStyle
        @Composable
        get() = appTheme.styles.scrollbarStyle

    object colors {
        val titleBarIconTint
            @Composable
            get() = appTheme.colors.titleBarIconTint

        val subTextColor: Color
            @Composable
            get() = appTheme.colors.subTextColor

        val dangerousActionColor: Color
            @Composable
            get() = appTheme.colors.dangerousActionColor

        val errorColor: Color
            @Composable
            get() = appTheme.colors.errorColor

        val accentColor: Color
            @Composable
            get() = appTheme.colors.accentColor

        val accentContainer: Color
            @Composable
            get() = appTheme.colors.accentContainer

        val borderColor: Color
            @Composable
            get() = appTheme.colors.borderColor

        val windowBackgroundColor: Color
            @Composable
            get() = appTheme.colors.windowBackgroundColor
    }

    object typography {
        val title : TextStyle
            @Composable
            get() = appTheme.typography.title

        val data: TextStyle
            @Composable
            get() = appTheme.typography.data
    }
}
