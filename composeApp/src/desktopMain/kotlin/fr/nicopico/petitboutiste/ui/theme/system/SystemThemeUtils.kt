/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.theme.system

private const val AWT_APP_APPEARANCE = "apple.awt.application.appearance"

fun followSystemTheme() {
    System.setProperty(AWT_APP_APPEARANCE, "system")
}
