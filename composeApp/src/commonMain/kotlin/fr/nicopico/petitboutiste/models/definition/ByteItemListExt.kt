/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.isOff
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

private val prettyJson = Json { prettyPrint = true }

// False-positive RedundantSuspendModifier with type-enabled analysis
@Suppress("RedundantSuspendModifier", "RedundantSuppression")
suspend fun List<ByteItem>.toJsonData(): String {
    val payloadEntries = filterIsInstance<ByteGroup>()
        .mapNotNull { byteGroup ->
            val definition = byteGroup.definition

            val renderedValue = if (!definition.representation.isOff && definition.representation.isReady) {
                byteGroup.getOrComputeRendering()
            } else null

            when {
                definition.name == null -> null
                definition.representation.isOff -> null
                renderedValue is RenderResult.Simple -> {
                    definition.name to JsonPrimitive(renderedValue.data)
                }
                renderedValue is RenderResult.Structured -> {
                    definition.name to renderedValue.data
                }
                else -> null
            }
        }
    return prettyJson.encodeToString(payloadEntries.toMap())
}
