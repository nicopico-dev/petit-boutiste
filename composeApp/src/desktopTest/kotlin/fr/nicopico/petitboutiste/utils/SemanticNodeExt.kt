/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.printToLog

fun SemanticsNodeInteraction.debug(
    tag: String = "PTB",
): SemanticsNodeInteraction = apply {
    printToLog(tag)
}
