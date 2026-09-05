/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import fr.nicopico.petitboutiste.models.representation.Representation
import kotlinx.io.files.Path
import kotlin.uuid.Uuid

/**
 * Represents a unique identifier for a tab
 */
@JvmInline
@Immutable
value class TabId(val value: String) {
    companion object {
        fun create(): TabId = TabId(Uuid.random().toString())
    }
}

/**
 * Represents the data for a single tab, including its input data, input type, and group definitions
 */
@Stable
data class TabData(
    val id: TabId = TabId.create(),
    val name: String? = null,
    val rendering: TabDataRendering = TabDataRendering(),
    val scratchpad: String = "",
    val defaultRepresentation: Representation = DEFAULT_REPRESENTATION,
    val templateData: TabTemplateData? = null,
)

@Immutable
data class TabDataRendering(
    val inputData: DataString = HexString(""),
    val groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    val byteItems: List<ByteItem> = emptyList(),
    val errors: Map<String, String> = emptyMap(),
    val variableRegistry: DefinitionVariableRegistry? = null,
)

@Immutable
data class TabTemplateData(
    val templateFilePath: Path,
    val definitionsHaveChanged: Boolean = false,
)
