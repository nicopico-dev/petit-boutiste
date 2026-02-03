/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

data class SnackbarState(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
)
