/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.system

import fr.nicopico.petitboutiste.system.bridges.DefaultBridge
import fr.nicopico.petitboutiste.system.bridges.MacosBridge
import kotlinx.coroutines.flow.Flow
import org.jetbrains.skiko.SystemTheme

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
internal fun getSystemBridge(): SystemBridge {
    val osName = System.getProperty("os.name")
    return when {
        osName.contains("mac", ignoreCase = true) -> MacosBridge
        else -> DefaultBridge
    }
}
