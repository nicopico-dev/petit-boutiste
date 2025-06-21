package fr.nicopico.petitboutiste.utils.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class IntRangeSerializer : KSerializer<IntRange> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntRange", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: IntRange) {
        val string = with(value) {
            "$start..$endInclusive"
        }
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): IntRange {
        val string = decoder.decodeString()
        val values = string.split("..")
            .map { it.toInt() }
        return IntRange(values[0], values[1])
    }
}
