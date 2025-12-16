/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import kotlinx.serialization.Serializable
import java.awt.Toolkit

@Serializable
data class ScreenCharacteristics(
    val width: Int,
    val height: Int,
    val density: Float,
)

@Composable
fun getScreenCharacteristics(): ScreenCharacteristics {
    // NOTE: Toolkit is desktop only
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val density = LocalDensity.current.density

    return ScreenCharacteristics(
        width = screenSize.width,
        height = screenSize.height,
        density = density,
    )
}
