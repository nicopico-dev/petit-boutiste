/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences
import kotlin.reflect.KClass

actual fun getSettingsFor(clazz: KClass<*>): Settings = PreferencesSettings(
    Preferences.userNodeForPackage(clazz.java)
)
