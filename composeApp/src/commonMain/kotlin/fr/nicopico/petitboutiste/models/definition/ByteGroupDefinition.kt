/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.calculator.Calculator
import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import fr.nicopico.petitboutiste.models.representation.Representation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.Uuid

/**
 * Represents the boundaries of a byte group definition using formulas.
 * Exactly one of the three valid combinations must be used:
 * - StartAndEnd: startFormula + endFormula
 * - StartAndLength: startFormula + lengthFormula
 * - EndAndLength: endFormula + lengthFormula
 */
@Serializable(with = ByteGroupBoundariesSerializer::class)
sealed class ByteGroupBoundaries {
    abstract val startFormula: String?
    abstract val endFormula: String?
    abstract val lengthFormula: String?

    @Serializable
    data class StartAndEnd(
        override val startFormula: String,
        override val endFormula: String,
    ) : ByteGroupBoundaries() {
        override val lengthFormula: String? = null
    }

    @Serializable
    data class StartAndLength(
        override val startFormula: String,
        override val lengthFormula: String,
    ) : ByteGroupBoundaries() {
        override val endFormula: String? = null
    }

    @Serializable
    data class EndAndLength(
        override val endFormula: String,
        override val lengthFormula: String,
    ) : ByteGroupBoundaries() {
        override val startFormula: String? = null
    }

    companion object {
        fun fromFormulas(
            startFormula: String? = null,
            endFormula: String? = null,
            lengthFormula: String? = null,
        ): ByteGroupBoundaries {
            return when {
                startFormula != null && endFormula != null && lengthFormula == null ->
                    StartAndEnd(startFormula, endFormula)
                startFormula != null && lengthFormula != null && endFormula == null ->
                    StartAndLength(startFormula, lengthFormula)
                endFormula != null && lengthFormula != null && startFormula == null ->
                    EndAndLength(endFormula, lengthFormula)
                else -> error("Invalid boundary combination: exactly one of (start+end), (start+length), or (end+length) must be defined")
            }
        }

        fun fromRange(indexes: IntRange): ByteGroupBoundaries {
            require(indexes.first >= 0) { "Start index must be non-negative, was ${indexes.first}" }
            require(indexes.first <= indexes.last) {
                "Start index must be <= end index, was ${indexes.first}..${indexes.last}"
            }
            return StartAndEnd(indexes.first.toString(), indexes.last.toString())
        }

        fun fromStartAndLength(start: Int, length: Int): ByteGroupBoundaries {
            require(start >= 0) { "Start index must be non-negative, was $start" }
            require(length > 0) { "Length must be positive, was $length" }
            return StartAndLength(start.toString(), length.toString())
        }

        fun fromEndAndLength(end: Int, length: Int): ByteGroupBoundaries {
            require(end >= 0) { "End index must be non-negative, was $end" }
            require(length > 0) { "Length must be positive, was $length" }
            return EndAndLength(end.toString(), length.toString())
        }
    }
}

object ByteGroupBoundariesSerializer : KSerializer<ByteGroupBoundaries> {
    override val descriptor: SerialDescriptor = ByteGroupBoundariesSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ByteGroupBoundaries) {
        val surrogate = when (value) {
            is ByteGroupBoundaries.StartAndEnd -> ByteGroupBoundariesSurrogate(
                startFormula = value.startFormula,
                endFormula = value.endFormula,
                lengthFormula = null,
            )
            is ByteGroupBoundaries.StartAndLength -> ByteGroupBoundariesSurrogate(
                startFormula = value.startFormula,
                endFormula = null,
                lengthFormula = value.lengthFormula,
            )
            is ByteGroupBoundaries.EndAndLength -> ByteGroupBoundariesSurrogate(
                startFormula = null,
                endFormula = value.endFormula,
                lengthFormula = value.lengthFormula,
            )
        }
        encoder.encodeSerializableValue(ByteGroupBoundariesSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): ByteGroupBoundaries {
        val surrogate = decoder.decodeSerializableValue(ByteGroupBoundariesSurrogate.serializer())
        return ByteGroupBoundaries.fromFormulas(
            startFormula = surrogate.startFormula,
            endFormula = surrogate.endFormula,
            lengthFormula = surrogate.lengthFormula,
        )
    }

    @Serializable
    private data class ByteGroupBoundariesSurrogate(
        val startFormula: String? = null,
        val endFormula: String? = null,
        val lengthFormula: String? = null,
    )
}

/**
 * Expanded boundaries with all formulas resolved (shortcuts replaced).
 */
@Serializable
private data class ExpandedBoundaries(
    val startFormula: String?,
    val endFormula: String?,
    val lengthFormula: String?,
) {
    companion object {
        fun fromBoundaries(boundaries: ByteGroupBoundaries): ExpandedBoundaries {
            val expandedStart = boundaries.startFormula?.replace("[[end]]", "(${boundaries.endFormula ?: "0"})")
                ?.replace("[[length]]", "(${boundaries.lengthFormula ?: "0"})")
            val expandedEnd = boundaries.endFormula?.replace("[[start]]", "(${boundaries.startFormula ?: "0"})")
                ?.replace("[[length]]", "(${boundaries.lengthFormula ?: "0"})")
            val expandedLength = boundaries.lengthFormula?.replace("[[start]]", "(${boundaries.startFormula ?: "0"})")
                ?.replace("[[end]]", "(${boundaries.endFormula ?: "0"})")
            return ExpandedBoundaries(expandedStart, expandedEnd, expandedLength)
        }
    }
}

@Serializable(with = ByteGroupDefinitionSerializer::class)
data class ByteGroupDefinition(
    val boundaries: ByteGroupBoundaries,
    val name: String? = null,
    val representation: Representation = DEFAULT_REPRESENTATION,
    val id: String = createDefinitionId(),
) {
    val startFormula: String? get() = boundaries.startFormula
    val endFormula: String? get() = boundaries.endFormula
    val lengthFormula: String? get() = boundaries.lengthFormula

    companion object {
        fun createFromRange(
            indexes: IntRange,
            name: String? = null,
            representation: Representation = DEFAULT_REPRESENTATION,
        ): ByteGroupDefinition {
            return ByteGroupDefinition(
                boundaries = ByteGroupBoundaries.fromRange(indexes),
                name = name,
                representation = representation,
            )
        }

        fun createFromStartAndLength(
            start: Int,
            length: Int,
            name: String? = null,
            representation: Representation = DEFAULT_REPRESENTATION,
        ): ByteGroupDefinition {
            return ByteGroupDefinition(
                boundaries = ByteGroupBoundaries.fromStartAndLength(start, length),
                name = name,
                representation = representation,
            )
        }

        fun createFromEndAndLength(
            end: Int,
            length: Int,
            name: String? = null,
            representation: Representation = DEFAULT_REPRESENTATION,
        ): ByteGroupDefinition {
            return ByteGroupDefinition(
                boundaries = ByteGroupBoundaries.fromEndAndLength(end, length),
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
            lengthFormula = value.lengthFormula,
            name = value.name,
            representation = value.representation,
            id = value.id
        )
        encoder.encodeSerializableValue(ByteGroupDefinitionSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): ByteGroupDefinition {
        val surrogate = decoder.decodeSerializableValue(ByteGroupDefinitionSurrogate.serializer())
        return ByteGroupDefinition(
            boundaries = ByteGroupBoundaries.fromFormulas(
                startFormula = surrogate.startFormula,
                endFormula = surrogate.endFormula,
                lengthFormula = surrogate.lengthFormula,
            ),
            name = surrogate.name,
            representation = surrogate.representation,
            id = surrogate.id
        )
    }

    @Serializable
    private data class ByteGroupDefinitionSurrogate(
        val startFormula: String? = null,
        val endFormula: String? = null,
        val lengthFormula: String? = null,
        val name: String? = null,
        val representation: Representation = DEFAULT_REPRESENTATION,
        val id: String = createDefinitionId(),
    )
}

fun ByteGroupDefinition.expandFormulas(): ByteGroupDefinition {
    val expandedBoundaries = ExpandedBoundaries.fromBoundaries(boundaries)
    return this.copy(
        boundaries = ByteGroupBoundaries.fromFormulas(
            startFormula = expandedBoundaries.startFormula,
            endFormula = expandedBoundaries.endFormula,
            lengthFormula = expandedBoundaries.lengthFormula,
        )
    )
}

/**
 * Resolves the start and end indexes from the formulas.
 * Handles all three valid combinations:
 * - startFormula + endFormula
 * - startFormula + lengthFormula (end = start + length - 1)
 * - endFormula + lengthFormula (start = end - length + 1)
 */
fun ByteGroupDefinition.resolveIndexes(variables: Map<String, Int>): IntRange {
    val expanded = expandFormulas()
    val calculatorStart = expanded.startFormula?.let { Calculator.computeOrThrow(it, variables) }
    val calculatorEnd = expanded.endFormula?.let { Calculator.computeOrThrow(it, variables) }
    val calculatorLength = expanded.lengthFormula?.let { Calculator.computeOrThrow(it, variables) }

    return when {
        calculatorStart != null && calculatorEnd != null -> {
            IntRange(calculatorStart, calculatorEnd)
        }
        calculatorStart != null && calculatorLength != null -> {
            IntRange(calculatorStart, calculatorStart + calculatorLength - 1)
        }
        calculatorEnd != null && calculatorLength != null -> {
            IntRange(calculatorEnd - calculatorLength + 1, calculatorEnd)
        }
        else -> error("Invalid ByteGroupDefinition: cannot resolve indexes from formulas")
    }
}

fun createDefinitionId(): String = Uuid.random().toString()
