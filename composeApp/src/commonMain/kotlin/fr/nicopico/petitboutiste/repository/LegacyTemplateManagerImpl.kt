/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.models.persistence.Template
import kotlinx.serialization.json.Json
import java.io.File
import java.util.prefs.Preferences

class LegacyTemplateManagerImpl(
    private val settings: Settings = PreferencesSettings(
        Preferences.userNodeForPackage(LegacyTemplateManager::class.java)
    ),
    private val json: Json = Json {
        ignoreUnknownKeys = true
    },
    private val templateManager: TemplateManager,
) : LegacyTemplateManager {

    override suspend fun exportLegacyTemplates(outputFolder: File) {
        val jsonData = settings.getStringOrNull(LEGACY_TEMPLATE_SETTINGS_KEY)
            ?: run {
                log("No legacy template settings found")
                return
            }

        val legacyTemplates = json.decodeFromString<List<Template>>(jsonData)
        legacyTemplates.saveTo(outputFolder)
    }

    override suspend fun deleteAllLegacyTemplates() {
        settings.remove(LEGACY_TEMPLATE_SETTINGS_KEY)
    }

    override suspend fun convertLegacyTemplates(legacyTemplatesBundle: File, outputFolder: File) {
        val jsonData = legacyTemplatesBundle.readText()
        val legacyTemplates = json.decodeFromString<List<Template>>(jsonData)
        legacyTemplates.saveTo(outputFolder)
    }

    private suspend fun List<Template>.saveTo(outputFolder: File) {
        // TODO Save multiple files in parallel ?
        for (template in this) {
            templateManager.saveTemplate(template, File(outputFolder, "${template.name.sanitizeFileName()}.json"))
        }
    }

    companion object {
        private const val LEGACY_TEMPLATE_SETTINGS_KEY = "TEMPLATES"

        // TODO Sanitize file name
        private fun String.sanitizeFileName(): String = this
    }
}
