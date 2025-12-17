/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempFile
import kotlin.io.path.relativeTo

@OptIn(ExperimentalSerializationApi::class)
class TemplateManagerImpl(
    private val json: Json = Json.Default,
) : TemplateManager {

    override suspend fun loadTemplate(templateFile: File): Template {
        val template = templateFile.inputStream().buffered().use { stream ->
            json.decodeFromStream<Template>(stream)
        }
        return template.updateFileArgumentPaths { relativePath ->
            File(templateFile.parentFile, relativePath).absolutePath
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
        val templateWithRelativePaths = template.updateFileArgumentPaths { absolutePath ->
            File(absolutePath).relativePath(directory)
        }

        try {
            tempFile.outputStream().buffered().use { stream ->
                json.encodeToStream<Template>(templateWithRelativePaths, stream)
            }
            tempFile.copyTo(templateFile, overwrite = overwrite)
        } finally {
            tempFile.delete()
        }
    }
}

private fun Template.updateFileArgumentPaths(
    transform: (String) -> String,
): Template {
    return copy(
        definitions = definitions.map { definition ->
            definition.copy(
                representation = definition.representation.updateFileArgumentPaths(transform)
            )
        }
    )
}

private fun Representation.updateFileArgumentPaths(
    transform: (String) -> String,
): Representation {
    val fileKeys = dataRenderer.arguments
        .filter { it.type is ArgumentType.FileType }
        .map { it.key }
    val updatedArgValues = argumentValues
        .mapValues { (key, value) ->
            if (key in fileKeys) {
                transform(value)
            } else value
        }

    return copy(argumentValues = updatedArgValues)
}

private fun File.relativePath(baseDir: Path): String {
    return absoluteFile.toPath().relativeTo(baseDir).toString()
}
