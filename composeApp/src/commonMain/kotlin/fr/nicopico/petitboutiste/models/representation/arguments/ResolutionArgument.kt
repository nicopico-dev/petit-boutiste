/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.arguments

import fr.nicopico.petitboutiste.models.representation.DataRenderer

val ResolutionArgument = DataRenderer.Argument(
    key = "resolution",
    label = "Resolution",
    type = ArgumentType.NumericType(
        Double::class,
        argValueConverter = { it.toDouble() },
        numberConverter = { it.toString() },
    ),
    defaultValue = "0.1",
    hint = "Coefficient used to convert an integer value to a decimal value",
)
