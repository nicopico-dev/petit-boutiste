/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.persistence

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class Template(
    val name: String,
    val definitions: List<ByteGroupDefinition> = emptyList(),
    val scratchpad: String = "",
)
