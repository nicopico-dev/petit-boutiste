/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.system.bridges

import fr.nicopico.petitboutiste.system.SystemBridge
import fr.nicopico.petitboutiste.system.exceptions.LoadingSystemBridgeLibraryException
import fr.nicopico.petitboutiste.system.internalLog
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

internal object MacosBridge : SystemBridge {

    private const val LIBRARY_NAME = "macos_bridge"
    private var sendLogToPlatform = true

    init {
        try {
            loadNativeLibrary(LIBRARY_NAME)
        } catch (e: LoadingSystemBridgeLibraryException) {
            internalLog("Cannot load MacosBridge native library", e)
        }
    }

    override fun log(msg: String) {
        if (!sendLogToPlatform) return
        try {
            @Suppress("DEPRECATION")
            jniLog(msg)
        } catch (e: UnsatisfiedLinkError) {
            sendLogToPlatform = false
            internalLog("Cannot send log to platform", e)
        }
    }

    override fun observeThemeChanges(): Flow<SystemTheme> {
        return notificationFlow.map { currentSystemTheme }
    }

    //region macOS Theme
    private val _notificationFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val subscriptionCount = AtomicInt(0)

    private val notificationFlow = _notificationFlow
        .onStart { startObservingTheme() }
        .onCompletion { stopObservingTheme() }

    private fun startObservingTheme() {
        if (subscriptionCount.fetchAndIncrement() == 0) {
            internalLog("Start observing MacOS theme changes")
            try {
                @Suppress("DEPRECATION")
                jniStartObservingTheme()
            } catch (e: UnsatisfiedLinkError) {
                internalLog("Unable to observe MacOS theme changes, native library is unavailable", e)
            }
        }
    }

    private fun stopObservingTheme() {
        if (subscriptionCount.decrementAndFetch() == 0) {
            internalLog("Stop observing MacOS theme changes")
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

    //region JNI
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
}
