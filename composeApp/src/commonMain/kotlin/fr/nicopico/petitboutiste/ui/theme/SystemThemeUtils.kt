package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import fr.nicopico.petitboutiste.log
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme
import java.awt.Toolkit
import java.beans.PropertyChangeListener

private const val AWT_APP_APPEARANCE = "apple.awt.application.appearance"

fun followSystemTheme() {
    System.setProperty(AWT_APP_APPEARANCE, "system")
}

@Composable
fun observeSystemTheme(): State<SystemTheme> {
    val systemTheme = remember {
        mutableStateOf(currentSystemTheme)
    }

    // Listen to Toolkit appearance changes
    // (require `System.setProperty("apple.awt.application.appearance", "system")` in main.kt)
    DisposableEffect(Unit) {
        val toolkit = Toolkit.getDefaultToolkit()

        val listener = PropertyChangeListener { event ->
            log("AWT : $event")
            if (event.propertyName == AWT_APP_APPEARANCE) {
                systemTheme.value = currentSystemTheme
            }
        }
        log("AWT: add listener $listener")
        toolkit.addPropertyChangeListener(AWT_APP_APPEARANCE, listener)

        onDispose {
            log("AWT: remove listener $listener")
            toolkit.removePropertyChangeListener(AWT_APP_APPEARANCE, listener)
        }
    }

    return systemTheme
}
