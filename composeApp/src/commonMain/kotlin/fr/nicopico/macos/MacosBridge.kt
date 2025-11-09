package fr.nicopico.macos

import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

// Note: the class with `external` functions cannot have any "real" functions
class MacosBridge {
    external fun jniObserveTheme(): Int
}

fun MacosBridge.observeTheme(): Int {
    ensureLibraryIsLoaded()
    return jniObserveTheme()
}

private const val LIBRARY_NAME = "macos_bridge"
private val libraryLoaded = AtomicBoolean(false)

private fun ensureLibraryIsLoaded() {
    if (libraryLoaded.compareAndSet(false, true)) {
        val osName = System.getProperty("os.name")

        if (osName.lowercase().startsWith("mac")) {
            // FIXME resourcesDir is null with tasks `desktopRun` and `desktopHotRun`
            //  https://github.com/JetBrains/compose-hot-reload/issues/343
            //  https://youtrack.jetbrains.com/issue/CMP-8800
            // Use `./gradlew buildAndCopyMacosBridge && ./gradlew run` as a workaround
            val resourcesDir = File(System.getProperty("compose.application.resources.dir"))
            val libraryPath = resourcesDir
                .resolve("libs/${System.mapLibraryName(LIBRARY_NAME)}")
                .absolutePath

            try {
                System.load(libraryPath)
            } catch (e: UnsatisfiedLinkError) {
                error("Could not load $LIBRARY_NAME: ${e.message}")
            }
        } else error("Platform `$osName` not supported !")
    }
}
