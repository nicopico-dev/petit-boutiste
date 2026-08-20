/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.utils.json.IntRangeSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.Uuid

@Serializable(with = ByteGroupDefinitionSerializer::class)
data class ByteGroupDefinition(
    val startFormula: String,
    val endFormula: String,
    val name: String? = null,
    val representation: Representation = DEFAULT_REPRESENTATION,
    val id: String = createDefinitionId(),
) {
    companion object {
        fun createFromRange(
            indexes: IntRange,
            name: String? = null,
            representation: Representation = DEFAULT_REPRESENTATION,
        ): ByteGroupDefinition {
            require(indexes.first >= 0) { "Start index must be non-negative, was ${indexes.first}" }
            require(indexes.first <= indexes.last) {
                "Start index must be <= end index, was ${indexes.first}..${indexes.last}"
            }
            return ByteGroupDefinition(
                startFormula = indexes.first.toString(),
                endFormula = indexes.last.toString(),
                name = name,
                representation = representation,
            )
        }

    }
}

object ByteGroupDefinitionSerializer : KSerializer<ByteGroupDefinition> {
    override val descriptor: SerialDescriptor = ByteGroupDefinitionSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ByteGroupDefinition) {
        val surrogate = ByteGroupDefinitionSurrogate(
            startFormula = value.startFormula,
            endFormula = value.endFormula,
            name = value.name,
            representation = value.representation,
            id = value.id
        )
        encoder.encodeSerializableValue(ByteGroupDefinitionSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): ByteGroupDefinition {
        val surrogate = decoder.decodeSerializableValue(ByteGroupDefinitionSurrogate.serializer())
        return if (surrogate.startFormula != null && surrogate.endFormula != null) {
            ByteGroupDefinition(
                startFormula = surrogate.startFormula,
                endFormula = surrogate.endFormula,
                name = surrogate.name,
                representation = surrogate.representation,
                id = surrogate.id
            )
        } else if (surrogate.indexes != null) {
            ByteGroupDefinition(
                startFormula = surrogate.indexes.start.toString(),
                endFormula = surrogate.indexes.endInclusive.toString(),
                name = surrogate.name,
                representation = surrogate.representation,
                id = surrogate.id
            )
        } else {
            error("Invalid ByteGroupDefinition: missing formulas or indexes")
        }
    }

    @Serializable
    private data class ByteGroupDefinitionSurrogate(
        val startFormula: String? = null,
        val endFormula: String? = null,
        @Serializable(with = IntRangeSerializer::class)
        val indexes: IntRange? = null,
        val name: String? = null,
        val representation: Representation = DEFAULT_REPRESENTATION,
        val id: String = createDefinitionId(),
    )
}

fun ByteGroupDefinition.expandFormulas(): ByteGroupDefinition {
    val expandedStart = startFormula.replace("[[end]]", "($endFormula)")
    val expandedEnd = endFormula.replace("[[start]]", "($startFormula)")
    return if (expandedStart == startFormula && expandedEnd == endFormula) {
        this
    } else {
        this.copy(startFormula = expandedStart, endFormula = expandedEnd)
    }
}

fun createDefinitionId(): String = Uuid.random().toString()
