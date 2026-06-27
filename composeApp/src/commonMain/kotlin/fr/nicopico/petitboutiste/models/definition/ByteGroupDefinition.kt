/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import fr.nicopico.petitboutiste.models.representation.Representation
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

// TODO NPI Deserialize definitions with static indexes
@Serializable
data class ByteGroupDefinition(
    val startFormula: String,
    val endFormula: String,
    val name: String? = null,
    val representation: Representation = DEFAULT_REPRESENTATION,
    val id: String = createDefinitionId(),
) {
    // TODO NPI Keep this constructor ?
    @Deprecated("!!")
    constructor(
        indexes: IntRange,
        name: String? = null,
        representation: Representation = DEFAULT_REPRESENTATION,
        id: String = createDefinitionId(),
    ) : this(
        startFormula = indexes.first.toString(),
        endFormula = indexes.last.toString(),
        name = name,
        representation = representation,
        id = id,
    )
}

fun createDefinitionId(): String = Uuid.random().toString()
