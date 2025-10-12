package fr.nicopico.petitboutiste.models.persistence

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class Template(
    val name: String,
    val definitions: List<ByteGroupDefinition> = emptyList(),
    val scratchpad: String = "",
)
