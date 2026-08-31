/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.calculator.Calculator
import fr.nicopico.petitboutiste.calculator.models.VariableValues
import fr.nicopico.petitboutiste.calculator.models.emptyVariableValues

/**
 * Sort [ByteGroupDefinition]s by their resolved `start` value, the same way
 * [fr.nicopico.petitboutiste.models.data.toByteItems] resolves it: `[[start]]`/`[[end]]` shortcuts are
 * expanded and variables are resolved through [variables].
 *
 * Definitions whose `start` value cannot be resolved (unknown variable, invalid formula) are kept last,
 * in their original relative order.
 */
class ByteGroupDefinitionSorter(
    private val variables: VariableValues = emptyVariableValues(),
) : Comparator<ByteGroupDefinition> {

    override fun compare(o1: ByteGroupDefinition, o2: ByteGroupDefinition): Int {
        val start1 = o1.resolveStart()
        val start2 = o2.resolveStart()

        return when {
            start1 != null && start2 != null -> start1.compareTo(start2)
            start1 != null -> -1 // Resolved definitions before unresolvable ones
            start2 != null -> 1 // Unresolvable definitions after resolved ones
            else -> 0 // Keep original order when neither can be resolved
        }
    }

    private fun ByteGroupDefinition.resolveStart(): Int? =
        Calculator.compute(expandFormulas().startFormula, variables)
}
