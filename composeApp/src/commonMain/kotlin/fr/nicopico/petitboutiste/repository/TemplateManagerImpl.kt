/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import io.github.vinceglb.filekit.utils.toFile
import kotlinx.io.files.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempFile
import kotlin.io.path.relativeTo

@OptIn(ExperimentalSerializationApi::class)
class TemplateManagerImpl(
    private val json: Json = Json.Default,
) : TemplateManager {

    override suspend fun loadTemplate(templateFilePath: Path): Template {
        val templateFile = templateFilePath.toFile()
        val template = templateFile.inputStream().buffered().use { stream ->
            json.decodeFromStream<Template>(stream)
        }
        return template.updateFileArgumentPaths { relativePath ->
            File(templateFile.parentFile, relativePath).toPath().normalize().toFile().absolutePath
        }
    }

    override suspend fun saveTemplate(
        template: Template,
        templateFilePath: Path,
        overwrite: Boolean,
    ) {
        val templateFile = templateFilePath.toFile()
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

@Suppress("UnusedPrivateMember")
private fun Representation.updateFileArgumentPaths(
    transform: (String) -> String,
): Representation {
    val fileKeys = dataRenderer.arguments
        .filter { it.type is ArgumentType.FileType }
        .map { it.key }
    val updatedArgValues = argumentValues
        .mapValues { (key, value) ->
            if (key in fileKeys) transform(value) else value
        }

    return copy(argumentValues = updatedArgValues)
}

private fun File.relativePath(baseDir: java.nio.file.Path): String {
    val filePath = absoluteFile.toPath()
    return try {
        filePath.relativeTo(baseDir).toString()
    } catch (_: IllegalArgumentException) {
        // Fallback to absolute path when paths do not share a common root
        filePath.toString()
    }
}
