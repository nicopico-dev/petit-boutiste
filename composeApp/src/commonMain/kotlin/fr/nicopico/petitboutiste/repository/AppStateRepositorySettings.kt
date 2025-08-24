package fr.nicopico.petitboutiste.repository

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.logError
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.app.AppState
import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences

class AppStateRepositorySettings(
    settings: Settings = PreferencesSettings(
        Preferences.userNodeForPackage(AppStateRepository::class.java)
    ),
    json: Json = Json {
        allowStructuredMapKeys = true
    }
) : BaseRepositorySettings(settings, json), AppStateRepository {

    private val key = "APP_STATE"

    override fun save(appState: AppState) {
        val persisted = PersistedAppState(
            tabs = appState.tabs.map { it.toPersisted() },
            selectedTabId = appState.selectedTabId.value
        )
        encodeAndStore(key, persisted)
    }

    override fun restore(): AppState {
        val persisted = decodeOrNull<PersistedAppState>(key)
            ?: return AppState().also { log("Restoring app state (default) -> $it") }

        val tabs = persisted.tabs.mapNotNull { pt ->
            try {
                pt.toTabData()
            } catch (e: Exception) {
                logError("Error restoring tab '${pt.id}': $e")
                null
            }
        }

        val finalTabs = if (tabs.isEmpty()) listOf(TabData()) else tabs
        val selected = TabId(persisted.selectedTabId)
        val selectedTabId = if (finalTabs.any { it.id == selected }) selected else finalTabs.first().id

        return AppState(
            tabs = finalTabs,
            selectedTabId = selectedTabId,
        ).also {
            log("Restoring app state -> $it")
        }
    }
}

@Serializable
private data class PersistedAppState(
    val tabs: List<PersistedTab>,
    val selectedTabId: String,
)

@Serializable
private data class PersistedTab(
    val id: String,
    val name: String,
    val inputHex: String,
    val inputType: InputType,
    val groupDefinitions: List<ByteGroupDefinition> = emptyList(),
)

private fun TabData.toPersisted(): PersistedTab = PersistedTab(
    id = id.value,
    name = name,
    inputHex = inputData.hexString,
    inputType = inputType,
    groupDefinitions = groupDefinitions,
)

private fun PersistedTab.toTabData(): TabData {
    val hex = HexString(inputHex)
    val data = when (inputType) {
        InputType.HEX -> hex
        InputType.BINARY -> BinaryString.fromHexString(hex)
    }
    return TabData(
        id = TabId(id),
        name = name,
        inputData = data,
        inputType = inputType,
        groupDefinitions = groupDefinitions,
    )
}
