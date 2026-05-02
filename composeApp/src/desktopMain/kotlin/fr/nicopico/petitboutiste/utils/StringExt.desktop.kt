/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

import fr.nicopico.petitboutiste.models.representation.TextCharset
import java.nio.charset.Charset

actual fun ByteArray.toString(charset: TextCharset): String {
    val jvmCharset = Charset.forName(charset.canonicalName)
    return String(this, jvmCharset)
}
