/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.asString
import fr.nicopico.petitboutiste.models.representation.isOff
import fr.nicopico.petitboutiste.models.representation.isReady
import kotlinx.serialization.json.Json

private val prettyJson = Json { prettyPrint = true }

suspend fun List<ByteItem>.toJsonData(): String {
    val payloadEntries = filterIsInstance<ByteGroup>()
        .mapNotNull { byteGroup ->
            val definition = byteGroup.definition

            val renderedValue = if (!definition.representation.isOff && definition.representation.isReady) {
                byteGroup.getOrComputeRendering().asString()
            } else null

            when {
                definition.name == null -> null
                definition.representation.isOff -> null
                else -> definition.name to renderedValue
            }
        }
    return prettyJson.encodeToString(payloadEntries.toMap())
}
