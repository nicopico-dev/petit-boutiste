package fr.nicopico.petitboutiste.models.representation

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

fun Representation.render(byteItem: ByteItem): String? {
    return try {
        dataRenderer.invoke(byteItem.toByteArray(), argumentValues)
    } catch (e: Exception) {
        // TODO Display the rendering error on the UI
        println("Render error: ${e.message}")
        null
    }
}
