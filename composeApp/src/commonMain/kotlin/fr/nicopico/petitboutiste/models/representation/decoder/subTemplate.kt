/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.analysis.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.analysis.ByteItem
import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.models.input.toByteItems
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.render
import fr.nicopico.petitboutiste.repository.TemplateManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
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
@Suppress("OPT_IN_USAGE")
private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}

fun DataRenderer.decodeSubTemplate(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.SubTemplate)
    val templateFile: File = getArgumentValue(ARG_TEMPLATE_FILE_KEY, argumentValues)!!

    val template = runBlocking {
        templateManager.loadTemplate(templateFile)
    }

    val dataString = HexString(byteArray.toHexString())
    val parsedData = dataString.toByteItems(template.definitions)
        .filterIsInstance<ByteItem.Group>()
        .associate { group ->
            val groupName = group.name ?: "UNNAMED (${group.definition.indexes})"
            val groupValue = group.definition.representation.render(group).output

            groupName to groupValue
        }

    return json.encodeToString(parsedData)
}

private val RenderResult.output: String
    get() = when (this) {
        is RenderResult.Error -> "ERROR($message)"
        is RenderResult.None -> ""
        is RenderResult.Success -> this.data
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
