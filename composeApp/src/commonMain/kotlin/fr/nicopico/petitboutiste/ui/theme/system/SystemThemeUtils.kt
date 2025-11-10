package fr.nicopico.petitboutiste.ui.theme.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import fr.nicopico.macos.MacosBridge
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

private const val AWT_APP_APPEARANCE = "apple.awt.application.appearance"

fun followSystemTheme() {
    System.setProperty(AWT_APP_APPEARANCE, "system")
}

@Composable
fun observeSystemTheme(): State<SystemTheme> {
    val systemTheme = remember {
        mutableStateOf(currentSystemTheme)
    }

    DisposableEffect(Unit) {
        MacosBridge.startObservingTheme()

        // TODO Update systemTheme

        onDispose {
            MacosBridge.stopObservingTheme()
        }
    }

    return systemTheme
}
