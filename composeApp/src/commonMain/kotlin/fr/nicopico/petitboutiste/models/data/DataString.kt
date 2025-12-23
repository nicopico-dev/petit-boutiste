/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.data

/**
 * Interface representing a data string.
 * This interface is implemented by classes that represent specific types of data strings,
 * such as hexadecimal strings.
 */
sealed interface DataString {
    /**
     * The normalized hexadecimal representation of the data.
     */
    val hexString: String

    /**
     * Checks if the data string is not empty.
     *
     * @return true if the data string is not empty, false otherwise.
     */
    fun isNotEmpty(): Boolean
}
