package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.ui.util.fromRGBAHexStringOrNull
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

object JewelThemeUtils {

    val lightIconTint = Color.fromRGBAHexStringOrNull("6C707EFF")!!
    val darkIconTint = Color.fromRGBAHexStringOrNull("CED0D6FF")!!

    val iconTint: Color
        get() = if (currentSystemTheme == SystemTheme.LIGHT) lightIconTint else darkIconTint
}
