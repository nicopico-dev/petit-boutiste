/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.persistence.Template
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempFile

@OptIn(ExperimentalSerializationApi::class)
class TemplateManagerImpl(
    private val json: Json = Json.Default,
) : TemplateManager {

    override suspend fun loadTemplate(templateFile: File): Template {
        return templateFile.inputStream().buffered().use { stream ->
            json.decodeFromStream<Template>(stream)
        }
    }

    override suspend fun saveTemplate(
        template: Template,
        templateFile: File,
        overwrite: Boolean,
    ) {
        val directory = templateFile.parentFile.toPath()
        directory.createDirectories()

        val tempFile = createTempFile(directory).toFile()

        try {
            tempFile.outputStream().buffered().use { stream ->
                json.encodeToStream<Template>(template, stream)
            }
            tempFile.copyTo(templateFile, overwrite = overwrite)
        } finally {
            tempFile.delete()
        }
    }
}
