package fr.nicopico.petitboutiste.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class Template(
    val id: Uuid = Uuid.random(),
    val name: String,
    val definitions: List<ByteGroupDefinition> = emptyList(),
)
