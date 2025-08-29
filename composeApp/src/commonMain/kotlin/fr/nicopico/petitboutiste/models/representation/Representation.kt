package fr.nicopico.petitboutiste.models.representation

import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.logError
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.extensions.toByteArray
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.emptyArgumentValues
import fr.nicopico.petitboutiste.utils.json.RepresentationSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

val DEFAULT_REPRESENTATION: Representation = Representation(DataRenderer.Off)

@OptIn(ExperimentalSerializationApi::class)
@Serializable(with = RepresentationSerializer::class)
data class Representation(
    val dataRenderer: DataRenderer,
    val argumentValues: ArgumentValues = emptyArgumentValues(),
)

val Representation.isOff: Boolean
    get() = dataRenderer == DataRenderer.Off


fun Representation.render(byteItem: ByteItem): RenderResult {
    // TODO Optimization: memoize the latest render to prevent multiple renderings of the same payload
    log("Rendering with $this...")
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
