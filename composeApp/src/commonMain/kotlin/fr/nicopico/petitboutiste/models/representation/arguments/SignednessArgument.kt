/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.arguments

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Signedness

private val DEFAULT: Signedness = Signedness.Signed

val SignednessArgument = DataRenderer.Argument(
    key = "signedness",
    label = "Signedness",
    type = ArgumentType.ChoiceType(
        type = Signedness::class,
        choices = Signedness.entries,
        argValueConverter = Signedness::valueOf,
        choiceConverter = Signedness::name,
    ),
    defaultValue = DEFAULT.name,
)

fun DataRenderer.getSignedness(argumentValues: ArgumentValues): Signedness {
    return getArgumentValue<Signedness>(SignednessArgument.key, argumentValues)
        ?: DEFAULT
}
