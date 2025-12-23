/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.utils.json.IntRangeSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ByteGroupDefinition(
    @Serializable(with = IntRangeSerializer::class)
    val indexes: IntRange,
    val name: String? = null,
    val representation: Representation = DEFAULT_REPRESENTATION,
    val id: String = createDefinitionId(),
) {
    init {
        require(indexes.first >= 0 && indexes.last >= indexes.first) {
            "ByteGroupDefinition indexes are invalid: $indexes"
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
fun createDefinitionId(): String = Uuid.random().toString()

object ByteGroupDefinitionSorter : Comparator<ByteGroupDefinition> {
    override fun compare(o1: ByteGroupDefinition, o2: ByteGroupDefinition): Int {
        return o1.indexes.first.compareTo(o2.indexes.first)
    }
}
