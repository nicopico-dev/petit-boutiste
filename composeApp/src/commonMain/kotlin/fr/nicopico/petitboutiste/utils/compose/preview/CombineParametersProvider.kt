/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

fun <A, B> combineParameterProviders(
    first: PreviewParameterProvider<A>,
    second: PreviewParameterProvider<B>,
): PreviewParameterProvider<Pair<A, B>> = object : PreviewParameterProvider<Pair<A, B>> {

    override val values: Sequence<Pair<A, B>>
        get() = first.values.flatMap { a ->
            second.values.map { b -> a to b }
        }
}
