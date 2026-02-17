/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.system

import fr.nicopico.petitboutiste.system.bridge.DefaultBridge
import fr.nicopico.petitboutiste.system.bridge.MacosBridge
import kotlinx.coroutines.flow.Flow
import org.jetbrains.skiko.SystemTheme

/**
 * Provides platform-specific system integration for the application.
 *
 * Implementations of this interface bridge to the underlying operating system
 * to perform tasks such as logging to the native system and observing changes
 * to the current system theme. The companion object exposes a singleton
 * instance that selects the appropriate platform implementation at runtime.
 */
interface SystemBridge {
    fun log(msg: String)
    fun observeThemeChanges(): Flow<SystemTheme>

    companion object : SystemBridge {
        private val instance by lazy {
            getSystemBridge().also { bridge ->
                internalLog("Using system bridge $bridge")
            }
        }

        override fun log(msg: String) {
            instance.log(msg)
        }

        override fun observeThemeChanges(): Flow<SystemTheme> {
            return instance.observeThemeChanges()
        }
    }
}

// Visible for testing
internal fun getSystemBridge(
    getSystemProperty: (key: String) -> String? = { key ->
        System.getProperty(key)
    },
): SystemBridge {
    val osName = getSystemProperty("os.name").orEmpty()
    return when {
        osName.contains("mac", ignoreCase = true) -> MacosBridge
        else -> DefaultBridge
    }
}
