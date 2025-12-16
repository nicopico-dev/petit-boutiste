/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.arguments

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness

private val DEFAULT: Endianness = Endianness.BigEndian

val EndiannessArgument = DataRenderer.Argument(
    key = "endianness",
    label = "Endianness",
    type = ArgumentType.ChoiceType(
        type = Endianness::class,
        choices = Endianness.entries,
        argValueConverter = Endianness::valueOf,
        choiceConverter = Endianness::name,
    ),
    defaultValue = DEFAULT.name,
    hint = """BigEndian: left-to-right
        |LittleEndian: right-to-left
    """.trimMargin()
)

fun DataRenderer.getEndianness(argumentValues: ArgumentValues): Endianness {
    return getArgumentValue<Endianness>(EndiannessArgument.key, argumentValues)
        ?: DEFAULT
}
