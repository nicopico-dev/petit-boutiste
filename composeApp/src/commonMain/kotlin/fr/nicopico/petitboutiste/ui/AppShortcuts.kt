package fr.nicopico.petitboutiste.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.FrameWindowScope
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppEvent.CycleTabEvent
import fr.nicopico.petitboutiste.utils.compose.Slot

/**
 * For shortcuts that are not tied to a menu item
 */
@Composable
@Suppress("UnusedReceiverParameter")
fun FrameWindowScope.AppShortcuts(
    onEvent: (AppEvent) -> Unit,
    content: Slot,
) {
    Box(
        modifier = Modifier.onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                val appEvent = handleKeyEvent(keyEvent)
                if (appEvent != null) {
                    onEvent(appEvent)
                    return@onKeyEvent true
                }
            }

            // Default
            false
        }
    ) {
        content()
    }
}

private fun handleKeyEvent(event: KeyEvent): AppEvent? {
    return when {
        // CTRL + SHIFT + Tab
        event.isCtrlPressed && event.isShiftPressed && event.key == Key.Tab -> CycleTabEvent(cycleForward = false)
        // CTRL + Tab
        event.isCtrlPressed && event.key == Key.Tab -> CycleTabEvent(cycleForward = true)
        else -> null
    }
}
