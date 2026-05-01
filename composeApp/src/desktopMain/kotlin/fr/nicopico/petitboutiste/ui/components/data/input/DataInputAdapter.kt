/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data.input

import fr.nicopico.petitboutiste.models.data.DataString

interface DataInputAdapter<T: DataString> {
    /**
     * Placeholder to show when empty
     */
    val placeholder: String

    /**
     * Parse [input] to the target value
     */
    fun parse(input: String): T?

    /**
     * Get a normalized String for [value]
     */
    fun getNormalizedString(value: T): String
}
