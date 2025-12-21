/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import fr.nicopico.petitboutiste.models.analysis.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.events.AppState
import fr.nicopico.petitboutiste.models.events.InputType
import fr.nicopico.petitboutiste.models.events.TabData
import fr.nicopico.petitboutiste.models.events.TabId
import fr.nicopico.petitboutiste.models.events.TabTemplateData
import fr.nicopico.petitboutiste.models.input.Base64String
import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import fr.nicopico.petitboutiste.utils.log
import fr.nicopico.petitboutiste.utils.logError
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
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
            selectedTabId = appState.selectedTabId.value,
            appTheme = appState.appTheme.name,
        )
        encodeAndStore(key, persisted)
    }

    override fun restore(): AppState {
        val persisted = decodeOrNull<PersistedAppState>(key)
            ?: return AppState().also { log("No persisted AppState, using default -> $it") }

        val tabs = persisted.tabs.mapNotNull { pt ->
            try {
                pt.toTabData()
            } catch (e: Exception) {
                logError("Error restoring tab '${pt.id}': $e")
                null
            }
        }

        val finalTabs = tabs.ifEmpty { listOf(TabData()) }
        val selected = TabId(persisted.selectedTabId)
        val selectedTabId = if (finalTabs.any { it.id == selected }) selected else finalTabs.first().id

        val appTheme = try {
            PBTheme.valueOf(persisted.appTheme)
        } catch (_: IllegalArgumentException) {
            PBTheme.System
        }

        return AppState(
            tabs = finalTabs,
            selectedTabId = selectedTabId,
            appTheme = appTheme,
        ).also {
            log("Restoring app state -> $it")
        }
    }
}

@Serializable
private data class PersistedAppState(
    val tabs: List<PersistedTab>,
    val selectedTabId: String,
    val appTheme: String = PBTheme.System.name,
)

@Serializable
private data class PersistedTab(
    val id: String,
    val name: String?,
    val inputHex: String,
    val inputType: InputType,
    val groupDefinitions: List<ByteGroupDefinition>,
    val scratchpad: String,
    val templateFilePath: String?,
    val templateDefinitionsChanged: Boolean,
)

private fun TabData.toPersisted(): PersistedTab = PersistedTab(
    id = id.value,
    name = name,
    inputHex = inputData.hexString,
    inputType = inputType,
    groupDefinitions = groupDefinitions,
    scratchpad = scratchpad,
    templateFilePath = templateData?.templateFile?.path,
    templateDefinitionsChanged = templateData?.definitionsHaveChanged ?: false,
)

private fun PersistedTab.toTabData(): TabData {
    val hex = HexString(inputHex)
    val data = when (inputType) {
        InputType.HEX -> hex
        InputType.BINARY -> BinaryString.fromHexString(hex)
        InputType.BASE64 -> Base64String.fromHexString(hex)
    }
    return TabData(
        id = TabId(id),
        name = name,
        inputData = data,
        inputType = inputType,
        groupDefinitions = groupDefinitions,
        scratchpad = scratchpad,
        templateData = templateFilePath?.let { path ->
            TabTemplateData(
                templateFile = File(path),
                definitionsHaveChanged = templateDefinitionsChanged
            )
        },
    )
}
