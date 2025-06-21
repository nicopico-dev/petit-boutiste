package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.utils.json.IntRangeSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ByteGroupDefinition(
    @Serializable(with = IntRangeSerializer::class)
    val indexes: IntRange,
    val name: String? = null,
)
