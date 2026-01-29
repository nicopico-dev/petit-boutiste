/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.macos

import fr.nicopico.petitboutiste.utils.logError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.fetchAndIncrement

object MacosBridge {

    //region JNI
    private const val LIBRARY_NAME = "macos_bridge"

    init {
        try {
            loadNativeLibrary(LIBRARY_NAME)
        } catch (_: ApplicationResourcesDirNotConfigured) {
            logError("Cannot load native bridge library, application resources dir is not configured")
        }
    }

    @JvmStatic
    @Deprecated("JNI only. Use log() instead")
    external fun jniLog(message: String)

    @JvmStatic
    @Deprecated(
        "JNI only. Use startObservingTheme() instead",
    )
    external fun jniStartObservingTheme()

    @JvmStatic
    @Deprecated(
        "JNI only. Use stopObservingTheme() instead",
    )
    external fun jniStopObservingTheme()

    @JvmStatic
    @Deprecated(
        "JNI only",
        level = DeprecationLevel.HIDDEN,
    )
    fun notifyThemeChanged() {
        _notificationFlow.tryEmit(Unit)
    }
    //endregion

    private var sendLogToPlatform = true

    fun log(msg: String) {
        if (!sendLogToPlatform) return
        try {
            @Suppress("DEPRECATION")
            jniLog(msg)
        } catch (_: UnsatisfiedLinkError) {
            sendLogToPlatform = false
            logError("Cannot send log to platform, native library is unavailable")
        }
    }

    //region macOS Theme
    private val _notificationFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val subscriptionCount = AtomicInt(0)

    private val notificationFlow = _notificationFlow
        .onStart { startObservingTheme() }
        .onCompletion { stopObservingTheme() }

    fun observeThemeChanges(): Flow<SystemTheme> {
        return notificationFlow.map { currentSystemTheme }
    }

    private fun startObservingTheme() {
        if (subscriptionCount.fetchAndIncrement() == 0) {
            log("Start observing MacOS theme changes")
            try {
                @Suppress("DEPRECATION")
                jniStartObservingTheme()
            } catch (_: UnsatisfiedLinkError) {
                logError("Unable to observe MacOS theme changes, native library is unavailable")
            }
        }
    }

    private fun stopObservingTheme() {
        if (subscriptionCount.decrementAndFetch() == 0) {
            log("Stop observing MacOS theme changes")
            try {
                @Suppress("DEPRECATION")
                jniStopObservingTheme()
            } catch (_: UnsatisfiedLinkError) {
                // no-op (native library is unavailable)
            }
        }

        check(subscriptionCount.load() >= 0) {
            "Subscription count for MacOS theme changes cannot be less than 0"
        }
    }
    //endregion
}
