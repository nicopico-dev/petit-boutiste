/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
