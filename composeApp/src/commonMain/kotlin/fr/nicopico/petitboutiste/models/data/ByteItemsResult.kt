/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

import fr.nicopico.petitboutiste.calculator.DefinitionVariableRegistry
import fr.nicopico.petitboutiste.calculator.models.VariableValues
import fr.nicopico.petitboutiste.models.definition.ByteItem

data class ByteItemsResult(
    val items: List<ByteItem>,
    val errors: Map<String, String>,
    val registry: DefinitionVariableRegistry,
    /**
     * The variable values that were resolved to compute [items]
     */
    val variables: VariableValues,
)
