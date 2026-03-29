/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation

import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.toByteArray
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.emptyArgumentValues
import fr.nicopico.petitboutiste.utils.logError
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

val DEFAULT_REPRESENTATION: Representation = Representation(DataRenderer.Off)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Representation(
    val dataRenderer: DataRenderer,
    val argumentValues: ArgumentValues = emptyArgumentValues(),
) {

    /**
     * A representation is ready if all the arguments required by the
     * data renderer have a default value or are provided by the user
     */
    val isReady: Boolean by lazy {
        dataRenderer.arguments
            .filter { it.defaultValue == null }
            .all { it.key in argumentValues }
    }
}

val Representation.isOff: Boolean
    get() = dataRenderer == DataRenderer.Off

suspend fun Representation.render(byteItem: ByteItem): RenderResult {
    require(isReady) { "Representation must be ready!" }
    return try {
        dataRenderer.invoke(byteItem.toByteArray(), argumentValues)
    } catch (e: Exception) {
        logError("Error rendering with $this", e)
        RenderResult.Error(e.toString())
    }
}

suspend fun Representation.renderAsString(byteItem: ByteItem): String? {
    return render(byteItem).asString()
}
