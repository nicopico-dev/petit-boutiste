package fr.nicopico.macos

import fr.nicopico.petitboutiste.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

object MacosBridge {

    //region JNI
    private const val LIBRARY_NAME = "macos_bridge"

    init {
        // FIXME Fail gracefully if the library is not available (aka do not crash!)
        loadNativeLibrary(LIBRARY_NAME)
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

    private val notificationFlow = _notificationFlow
        .onStart {
            log("Started observing theme...")
            @Suppress("DEPRECATION")
            jniStartObservingTheme()
        }
        .onCompletion {
            log("Stop observing theme...")
            @Suppress("DEPRECATION")
            jniStopObservingTheme()
        }

    fun observeThemeChanges(): Flow<SystemTheme> {
        return notificationFlow.map { currentSystemTheme }
    }
}
