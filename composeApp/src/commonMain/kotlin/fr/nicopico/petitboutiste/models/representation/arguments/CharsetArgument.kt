/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.arguments

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.TextCharset

private val DEFAULT: TextCharset = TextCharset.UTF_8

val CharsetArgument = DataRenderer.Argument(
    key = "charset",
    label = "Charset",
    type = ArgumentType.ChoiceType(
        type = TextCharset::class,
        choices = listOf(
            TextCharset.UTF_8,
            TextCharset.US_ASCII,
            TextCharset.ISO_8859_1,
            TextCharset.UTF_16BE,
            TextCharset.UTF_16LE,
            TextCharset.UTF_32BE,
            TextCharset.UTF_32LE,
        ),
        argValueConverter = { TextCharset.forName(it) },
        choiceConverter = TextCharset::canonicalName,
    ),
    defaultValue = DEFAULT.canonicalName,
)

fun DataRenderer.getCharset(argumentValues: ArgumentValues): TextCharset {
    return getArgumentValue<TextCharset>(CharsetArgument.key, argumentValues)
        ?: DEFAULT
}
