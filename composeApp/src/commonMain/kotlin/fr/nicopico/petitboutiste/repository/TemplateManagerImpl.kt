/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.utils.file.absolutePath
import fr.nicopico.petitboutiste.utils.file.asSink
import fr.nicopico.petitboutiste.utils.file.asSource
import fr.nicopico.petitboutiste.utils.file.createTempFile
import fr.nicopico.petitboutiste.utils.file.normalize
import fr.nicopico.petitboutiste.utils.file.parentOrCurrent
import fr.nicopico.petitboutiste.utils.file.relativeTo
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class TemplateManagerImpl(
    private val json: Json = Json.Default,
    private val fileSystem: FileSystem = SystemFileSystem,
) : TemplateManager {

    override suspend fun loadTemplate(templateFilePath: Path): Template {
        val template = templateFilePath.asSource(fileSystem)
            .use { source ->
                json.decodeFromString<Template>(source.readString())
            }

        // Transform each FileArg relative path to an absolute path
        return template.transformFileArgumentPaths { relativePath ->
            Path(templateFilePath.parentOrCurrent, relativePath)
                .normalize()
                .absolutePath
        }
    }

    override suspend fun saveTemplate(
        template: Template,
        templateFilePath: Path,
        overwrite: Boolean,
    ) {
        if (fileSystem.exists(templateFilePath) && !overwrite) {
            error("Template file '$templateFilePath' already exists")
        }

        val directory = templateFilePath.parentOrCurrent
        fileSystem.createDirectories(directory)

        // Transform each FileArg absolute path to a path relative to the template file
        val templateToSave = template
            .transformFileArgumentPaths { absolutePath ->
                Path(absolutePath).relativeTo(directory)
            }

        val workingFilePath = createTempFile(directory = directory)
        try {
            workingFilePath.asSink(fileSystem).use { sink ->
                val jsonString = json.encodeToString<Template>(templateToSave)
                sink.writeString(jsonString)
            }
            workingFilePath.asSource(fileSystem).use { source ->
                templateFilePath.asSink(fileSystem).use { sink ->
                    source.transferTo(sink)
                }
            }
        } finally {
            fileSystem.delete(workingFilePath)
        }
    }
}

private fun Template.transformFileArgumentPaths(
    transform: (String) -> String,
): Template {
    return copy(
        definitions = definitions
            .map { definition ->
                definition.copy(
                    representation = updateFileArgumentPaths(definition.representation, transform)
                )
            }
    )
}

// Detekt false-positive
@Suppress("UnusedPrivateMember")
private fun updateFileArgumentPaths(
    representation: Representation,
    transform: (String) -> String,
): Representation {
    val fileKeys = representation.dataRenderer.arguments
        .filter { it.type is ArgumentType.FileType }
        .map { it.key }
    val updatedArgValues = representation.argumentValues
        .mapValues { (key, value) ->
            if (key in fileKeys) transform(value) else value
        }

    return representation.copy(argumentValues = updatedArgValues)
}
