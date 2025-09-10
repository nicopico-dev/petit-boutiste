package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.ui.util.fromRgbaHexStringOrNull
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

object JewelThemeUtils {

    val iconOnLightTint = Color.fromRgbaHexStringOrNull("6C707EFF")!!
    val iconOnDarkTint = Color.fromRgbaHexStringOrNull("CED0D6FF")!!

    val subTextColor = Color.Gray
    val dangerousActionColor = Color.Red

    val iconTint: Color
        get() = if (currentSystemTheme == SystemTheme.LIGHT) iconOnLightTint else iconOnDarkTint
}
