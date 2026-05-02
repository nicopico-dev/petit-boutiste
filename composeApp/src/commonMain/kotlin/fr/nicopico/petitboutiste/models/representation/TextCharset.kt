/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation

enum class TextCharset(
    val canonicalName: String
) {
    UTF_8("UTF-8"),
    US_ASCII("US-ASCII"),
    ISO_8859_1("ISO-8859-1"),
    UTF_16BE("UTF-16BE"),
    UTF_16LE("UTF-16LE"),
    UTF_32BE("UTF-32BE"),
    UTF_32LE("UTF-32LE"),
    ;

    companion object {
        fun forName(name: String): TextCharset {
            return entries
                .firstOrNull {
                    it.canonicalName.equals(name, ignoreCase = true)
                }
                ?: error("Unsupported charset: $name")
        }
    }
}
