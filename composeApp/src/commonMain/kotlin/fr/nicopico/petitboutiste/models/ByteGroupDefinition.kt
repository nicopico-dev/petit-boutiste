package fr.nicopico.petitboutiste.models

import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.utils.json.IntRangeSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ByteGroupDefinition
@OptIn(ExperimentalUuidApi::class) constructor(
    @Serializable(with = IntRangeSerializer::class)
    val indexes: IntRange,
    val name: String? = null,
    val representation: Representation = DEFAULT_REPRESENTATION,
    val id: String = Uuid.random().toString(),
) {
    init {
        require(indexes.first >= 0 && indexes.last >= indexes.first) {
            "ByteGroupDefinition indexes are invalid: $indexes"
        }
    }
}
