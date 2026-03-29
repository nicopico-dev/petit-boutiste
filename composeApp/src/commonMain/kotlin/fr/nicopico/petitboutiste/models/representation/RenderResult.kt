/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

sealed class RenderResult {
    data object None: RenderResult()

    sealed class Success : RenderResult()

    data class Simple(
        val data: String
    ): Success()

    data class Structured(
        val data: JsonElement
    ) : Success()

    data class Error(val message: String) : RenderResult()
}

@Suppress("OPT_IN_USAGE")
private val jsonRenderer = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}
private val jsonSingleLineRenderer = Json

fun RenderResult.asString(
    singleLine: Boolean = false
): String? = when (this) {
    is RenderResult.Error -> null
    is RenderResult.None -> ""
    is RenderResult.Simple -> data
    is RenderResult.Structured -> {
        val renderer = if (singleLine) jsonSingleLineRenderer else jsonRenderer
        renderer.encodeToString(data)
    }
}

fun String.asSimple(): RenderResult.Simple = RenderResult.Simple(this)

@Suppress("OPT_IN_USAGE")
private val jsonParser = Json {
    ignoreUnknownKeys = true
}

fun String.asStructured(): RenderResult.Structured {
    val jsonElement = jsonParser.parseToJsonElement(this)
    return RenderResult.Structured(jsonElement)
}

fun String.asDynamic(): RenderResult {
    return when(val jsonElement = jsonParser.parseToJsonElement(this)) {
        is JsonNull, is JsonPrimitive -> RenderResult.Simple(this)
        is JsonArray, is JsonObject -> RenderResult.Structured(jsonElement)
    }
}
