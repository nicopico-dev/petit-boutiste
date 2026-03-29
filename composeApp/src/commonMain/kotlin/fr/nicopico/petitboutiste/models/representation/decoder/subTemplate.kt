/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.data.toByteItems
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.repository.TemplateManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.io.File

private const val ARG_TEMPLATE_FILE_KEY = "templateFile"

val subTemplateArguments = listOf(
    Argument(
        key = ARG_TEMPLATE_FILE_KEY,
        label = "PTB Template file",
        type = FileType,
    )
)

private val templateManager = TemplateManager()

suspend fun DataRenderer.decodeSubTemplate(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.SubTemplate)
    val templateFile: File = getArgumentValue(ARG_TEMPLATE_FILE_KEY, argumentValues)!!

    val template = templateManager.loadTemplate(templateFile)

    val dataString = HexString(byteArray.toHexString())
    val parsedData = dataString.toByteItems(template.definitions)
        .filterIsInstance<ByteGroup>()
        .filter { group ->
            // Ignore unnamed groups
            group.name != null
        }
        .mapNotNull { group ->
            val groupName = group.name!!

            val groupValue: JsonElement = when(
                val renderResult = group.getOrComputeRendering()
            ) {
                is RenderResult.None -> {
                    // Ignore group with no renderResult
                    return@mapNotNull null
                }
                is RenderResult.Simple -> JsonPrimitive(renderResult.data)
                is RenderResult.Structured -> renderResult.data
                is RenderResult.Error -> {
                    // Early return for any sub-template error
                    return "SUB-TEMPLATE ERROR in $groupName: ${renderResult.message}"
                }
            }

            groupName to groupValue
        }
        .associate { (key, value) -> key to value }

    return Json.encodeToString(parsedData)
}

fun Representation.getSubTemplateFile(): File? {
    return dataRenderer.getArgumentValue<File>(ARG_TEMPLATE_FILE_KEY, argumentValues)
}

fun Representation.getSubTemplateDefinitions(): List<ByteGroupDefinition> {
    val templateFile: File = getSubTemplateFile()
        ?: return emptyList()

    val template = runBlocking {
        templateManager.loadTemplate(templateFile)
    }
    return template.definitions
}
