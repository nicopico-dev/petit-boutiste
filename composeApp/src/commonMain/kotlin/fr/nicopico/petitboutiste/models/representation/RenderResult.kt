/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation

sealed class RenderResult {
    data object None: RenderResult()
    data class Success(val data: String) : RenderResult()
    data class Error(val message: String) : RenderResult()
}

fun RenderResult.asString(): String? = when (this) {
    is RenderResult.Error -> null
    is RenderResult.None -> ""
    is RenderResult.Success -> data
}
