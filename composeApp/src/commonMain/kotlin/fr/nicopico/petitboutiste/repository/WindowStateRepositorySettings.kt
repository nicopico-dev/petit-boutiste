package fr.nicopico.petitboutiste.repository

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.nullableString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences

class WindowStateRepositorySettings(
    settings: Settings = PreferencesSettings(
        Preferences.userNodeForPackage(WindowStateRepository::class.java)
    ),
    private val json: Json = Json.Default,
) : WindowStateRepository {

    private var jsonData by settings.nullableString("persistedState")

    override fun save(windowState: WindowState) {
        val persisted = PersistedWindowState(
            x = windowState.position.x.value,
            y = windowState.position.y.value,
            width = windowState.size.width.value,
            height = windowState.size.height.value,
            isMaximized = windowState.placement == WindowPlacement.Maximized,
            isFullScreen = windowState.placement == WindowPlacement.Fullscreen,
        )
        jsonData = json.encodeToString(persisted)
    }

    // TODO Save & Restore WindowState with respect with the screen characteristics
    override fun restore(): WindowState? {
        val persisted = jsonData
            ?.let { jsonData -> Json.decodeFromString<PersistedWindowState>(jsonData) }
            ?: return null

        return WindowState(
            position = if (persisted.x != null && persisted.y != null) {
                WindowPosition.Absolute(persisted.x.dp, persisted.y.dp)
            } else {
                WindowPosition.PlatformDefault
            },
            placement = when {
                persisted.isMaximized -> WindowPlacement.Maximized
                persisted.isFullScreen -> WindowPlacement.Fullscreen
                else -> WindowPlacement.Floating
            },
            width = persisted.width?.dp ?: 800.dp,
            height = persisted.height?.dp ?: 600.dp,
        )
    }

    @Serializable
    private data class PersistedWindowState(
        val x: Float?,
        val y: Float?,
        val width: Float?,
        val height: Float?,
        val isMaximized: Boolean = false,
        val isFullScreen: Boolean = false,
    )
}
