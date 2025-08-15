package fr.nicopico.petitboutiste.utils.json

import fr.nicopico.petitboutiste.models.Representation
import fr.nicopico.petitboutiste.models.renderer.DataRenderer
import fr.nicopico.petitboutiste.models.renderer.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.renderer.arguments.emptyArgumentValues
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.charset.Charset

@ExperimentalSerializationApi
object RepresentationSerializer : KSerializer<Representation> {

    @Serializable
    private data class RepresentationSurrogate(
        val dataRenderer: DataRenderer,
        val argumentValues: ArgumentValues = emptyArgumentValues(),
    )

    override val descriptor: SerialDescriptor = RepresentationSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Representation) {
        val surrogate = RepresentationSurrogate(value.dataRenderer, value.argumentValues)
        encoder.encodeSerializableValue(RepresentationSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Representation {
        return if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            val transformed = transformLegacy(element)
            val surrogate = decoder.json.decodeFromJsonElement(RepresentationSurrogate.serializer(), transformed)
            Representation(surrogate.dataRenderer, surrogate.argumentValues)
        } else {
            val surrogate = decoder.decodeSerializableValue(RepresentationSurrogate.serializer())
            Representation(surrogate.dataRenderer, surrogate.argumentValues)
        }
    }

    // Handle legacy format with RepresentationFormat
    private fun transformLegacy(element: JsonElement): JsonElement {
        val obj = element as? JsonObject ?: return element
        val type = obj["type"] ?: return element
        val typeName = type.jsonPrimitive.content
        fun rep(renderer: String, args: Map<String, String> = emptyMap()): JsonObject {
            return buildJsonObject {
                put("dataRenderer", JsonPrimitive(renderer))
                val argsObj = JsonObject(args.mapValues { JsonPrimitive(it.value) })
                put("argumentValues", argsObj)
            }
        }
        return when {
            typeName.endsWith(".Binary") -> rep("Binary")
            typeName.endsWith(".Hexadecimal") -> rep("Hexadecimal")
            typeName.endsWith(".Integer") -> {
                val endianness = obj["endianness"]?.jsonPrimitive?.content ?: "BigEndian"
                rep("Integer", mapOf("endianness" to endianness))
            }
            typeName.endsWith(".Text") -> {
                val endianness = obj["endianness"]?.jsonPrimitive?.content ?: "BigEndian"
                val charset = obj["charset"]?.jsonPrimitive?.content ?: Charset.forName("UTF-8").name()
                rep("Text", mapOf("endianness" to endianness, "charset" to charset))
            }
            typeName.endsWith(".Custom") -> {
                val renderer = obj["renderer"]?.jsonPrimitive?.content ?: "Off"
                val arguments = (obj["arguments"] as? JsonObject)?.mapValues { it.value.jsonPrimitive.content } ?: emptyMap()
                rep(renderer, arguments)
            }
            else -> element
        }
    }
}
