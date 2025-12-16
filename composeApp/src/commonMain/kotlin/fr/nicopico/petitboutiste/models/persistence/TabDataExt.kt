package fr.nicopico.petitboutiste.models.persistence

import fr.nicopico.petitboutiste.models.ui.TabData
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun TabData.toTemplate(
    fallbackName: String
) = Template(
    name = name ?: fallbackName,
    definitions = groupDefinitions,
    scratchpad = scratchpad,
)
