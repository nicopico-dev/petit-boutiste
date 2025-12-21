/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation

import fr.nicopico.petitboutiste.models.analysis.ByteItem
import fr.nicopico.petitboutiste.models.analysis.toByteArray
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.emptyArgumentValues
import fr.nicopico.petitboutiste.utils.logError
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

val DEFAULT_REPRESENTATION: Representation = Representation(DataRenderer.Off)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Representation(
    val dataRenderer: DataRenderer,
    val argumentValues: ArgumentValues = emptyArgumentValues(),
    @Transient
    val submitCount: Int = 0,
)

val Representation.submitted: Boolean
    get() = submitCount > 0

val Representation.isOff: Boolean
    get() = dataRenderer == DataRenderer.Off

/**
 * A representation is ready if:
 * the representation does not require any user validation
 * *or*
 * the user explicitly submitted the representation for rendering
 */
val Representation.isReady: Boolean
    get() = !dataRenderer.requireUserValidation || submitted

fun Representation.render(byteItem: ByteItem): RenderResult {
    // TODO Optimization: memoize the latest render to prevent multiple renderings of the same payload
    require(isReady) { "Representation must be ready!" }
    return try {
        dataRenderer.invoke(byteItem.toByteArray(), argumentValues)
            ?.let { render -> RenderResult.Success(render) }
            ?: RenderResult.None
    } catch (e: Exception) {
        logError("Error rendering with $this: ${e.message}")
        RenderResult.Error(e.toString())
    }
}

fun Representation.renderAsString(byteItem: ByteItem): String? {
    return render(byteItem).asString()
}
