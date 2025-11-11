package fr.nicopico.macos

import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.logError
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
}
