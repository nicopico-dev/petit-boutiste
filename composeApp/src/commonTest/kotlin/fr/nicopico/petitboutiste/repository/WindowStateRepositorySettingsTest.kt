/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.russhwolf.settings.MapSettings
import fr.nicopico.petitboutiste.models.persistence.ScreenCharacteristics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class WindowStateRepositorySettingsTest {

    private val settings = MapSettings()
    private val repository = WindowStateRepositorySettings(settings)

    private val screen1 = ScreenCharacteristics(1920, 1080, 1.0f)
    private val screen2 = ScreenCharacteristics(3840, 2160, 2.0f)

    @Test
    fun `saves and restores window state for a specific screen`() {
        // Given
        val windowState = WindowState(
            position = WindowPosition.Absolute(100.dp, 200.dp),
            size = DpSize(800.dp, 600.dp),
            placement = WindowPlacement.Floating
        )

        // When
        repository.save(windowState, screen1)
        val restored = repository.restore(screen1)

        // Then
        assertNotNull(restored)
        assertEquals(100.dp, (restored.position as WindowPosition.Absolute).x)
        assertEquals(200.dp, (restored.position as WindowPosition.Absolute).y)
        assertEquals(800.dp, restored.size.width)
        assertEquals(600.dp, restored.size.height)
        assertEquals(WindowPlacement.Floating, restored.placement)
    }

    @Test
    fun `restores different states for different screens`() {
        // Given
        val state1 = WindowState(
            position = WindowPosition.Absolute(0.dp, 0.dp),
            size = DpSize(1920.dp, 1080.dp),
            placement = WindowPlacement.Maximized
        )
        val state2 = WindowState(
            position = WindowPosition.Absolute(100.dp, 100.dp),
            size = DpSize(800.dp, 600.dp),
            placement = WindowPlacement.Floating
        )

        // When
        repository.save(state1, screen1)
        repository.save(state2, screen2)

        val restored1 = repository.restore(screen1)
        val restored2 = repository.restore(screen2)

        // Then
        assertNotNull(restored1)
        assertEquals(WindowPlacement.Maximized, restored1.placement)

        assertNotNull(restored2)
        assertEquals(WindowPlacement.Floating, restored2.placement)
        assertEquals(800.dp, restored2.size.width)
    }

    @Test
    fun `returns null when no state is persisted for a screen`() {
        // When
        val restored = repository.restore(screen1)

        // Then
        assertNull(restored)
    }

    @Test
    fun `saves and restores full screen placement`() {
        // Given
        val windowState = WindowState(
            placement = WindowPlacement.Fullscreen
        )

        // When
        repository.save(windowState, screen1)
        val restored = repository.restore(screen1)

        // Then
        assertNotNull(restored)
        assertEquals(WindowPlacement.Fullscreen, restored.placement)
    }
}
