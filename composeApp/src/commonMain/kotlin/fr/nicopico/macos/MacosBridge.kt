package fr.nicopico.macos

import fr.nicopico.petitboutiste.log
import kotlin.concurrent.atomics.AtomicBoolean

object MacosBridge {

    private const val LIBRARY_NAME = "macos_bridge"

    init {
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
        log("Theme has changed!")
    }

    //region Public API
    private val observing = AtomicBoolean(false)

    fun startObservingTheme() {
        if (observing.compareAndSet(expectedValue = false, newValue = true)) {
            log("Started observing theme...")
            @Suppress("DEPRECATION")
            jniStartObservingTheme()
        }
    }

    fun stopObservingTheme() {
        if (observing.compareAndSet(expectedValue = true, newValue = false)) {
            log("Stop observing theme...")
            @Suppress("DEPRECATION")
            jniStopObservingTheme()
        }
    }
    //endregion
}
