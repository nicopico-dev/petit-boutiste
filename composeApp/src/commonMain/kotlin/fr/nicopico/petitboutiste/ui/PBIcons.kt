package fr.nicopico.petitboutiste.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.jewel.ui.component.painterResource
import org.jetbrains.jewel.ui.icon.PathIconKey

object PBIcons {

    val app: Painter
        @Composable
        @Suppress("DEPRECATION")
        get() = painterResource("icons/app-icon.png")

    val themeDark: PathIconKey = PathIconKey("icons/themeDark.svg", PBIcons::class.java)
    val themeLight: PathIconKey = PathIconKey("icons/themeLight.svg", PBIcons::class.java)
    val themeSystem: PathIconKey = PathIconKey("icons/themeSystem.svg", PBIcons::class.java)
}
