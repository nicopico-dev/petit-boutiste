package fr.nicopico.macos

import fr.nicopico.petitboutiste.log

object MacosBridge {

    private const val LIBRARY_NAME = "macos_bridge"

    init {
        loadNativeLibrary(LIBRARY_NAME)
    }

    @JvmStatic
    external fun jniStartObservingTheme()

    @JvmStatic
    external fun jniStopObservingTheme()

    @JvmStatic
    fun notifyThemeChanged() {
        log("Theme has changed!")
    }
}
