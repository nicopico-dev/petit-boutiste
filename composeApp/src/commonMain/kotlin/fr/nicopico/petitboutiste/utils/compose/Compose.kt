package fr.nicopico.petitboutiste.utils.compose

import androidx.compose.runtime.Composable

typealias Slot = @Composable () -> Unit

/**
 * Utility method to return a Composable if and only if the receiver is not null
 *
 * These two lines are equivalent
 * ```kotlin
 * someText?.let { { Text(it) } }
 * someText?.compose { Text(it) }
 * ```
 */
fun <T : Any> T?.optionalSlot(block: @Composable (T) -> Unit) : Slot? {
    return if (this != null) {
        { block(this) }
    } else null
}
