package fr.nicopico.petitboutiste.utils

import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.ui.util.fromArgbHexStringOrNull

fun hexColor(argb: String): Color? {
    return Color.fromArgbHexStringOrNull(argb)
}
