/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.asString
import fr.nicopico.petitboutiste.models.representation.isOff
import fr.nicopico.petitboutiste.models.representation.isReady

suspend fun buildJson(
    definitions: List<ByteGroupDefinition>,
    byteItems: List<ByteItem>,
): String {
    val payloadEntries = definitions.mapNotNull { definition ->
        val byteGroup = byteItems.firstOrNull {
            it is ByteGroup && it.definition == definition
        } as? ByteGroup

        val renderedValue = if (
            byteGroup != null
            && !definition.representation.isOff
            && definition.representation.isReady
        ) {
            byteGroup.getOrComputeRendering().asString()
        } else null

        when {
            definition.name == null -> null
            definition.representation.isOff -> null
            else -> definition.name to renderedValue
        }
    }
    return buildJsonLikePayloads(payloadEntries)
}

private fun buildJsonLikePayloads(entries: List<Pair<String, String?>>): String {
    if (entries.isEmpty()) return "{}"

    val content = entries.joinToString(separator = ",\n") { (name, rendered) ->
        val escapedName = name.escapeJsonLike()
        val renderedValue = rendered?.let { "\"${it.escapeJsonLike()}\"" } ?: "null"
        "  \"$escapedName\": $renderedValue"
    }
    return "{\n$content\n}"
}

private fun String.escapeJsonLike(): String {
    val source = this
    return buildString(source.length) {
        source.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
    }
}
