/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.models.input.HexString

/**
 * Convert HexString to BinaryString
 */
fun HexString.toBinaryString(): BinaryString {
    return BinaryString.fromHexString(this)
}

/**
 * Format binary string for display (group by bytes with subgroups of 4 bits)
 * Example: "01010101" becomes "0101 0101"
 * Example: "0101010101010101" becomes "0101 0101 0101 0101"
 */
fun BinaryString.formatForDisplay(): String {
    return binaryString.chunked(8) { byte ->
        "${byte.take(4)} ${byte.substring(4)}"
    }.joinToString(" ")
}
