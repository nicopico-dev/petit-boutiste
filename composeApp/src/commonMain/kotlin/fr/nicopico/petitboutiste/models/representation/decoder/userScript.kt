/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.DataRenderer.Argument
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType.FileType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues

const val ARG_USER_SCRIPT_FILE_KEY = "protoFile"

val userScriptArguments = listOf(
    Argument(
        key = ARG_USER_SCRIPT_FILE_KEY,
        label = "script file",
        type = FileType,
    ),
)

expect suspend fun DataRenderer.decodeUserScript(byteArray: ByteArray, argumentValues: ArgumentValues): String
