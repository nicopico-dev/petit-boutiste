package fr.nicopico.macos

import java.io.File

fun loadNativeLibrary(libraryName: String) {
    val osName = System.getProperty("os.name")

    if (osName.lowercase().startsWith("mac")) {
        // FIXME resourcesDir is null with tasks `desktopRun` and `desktopHotRun`
        //  https://github.com/JetBrains/compose-hot-reload/issues/343
        //  https://youtrack.jetbrains.com/issue/CMP-8800
        // Use `./gradlew buildAndCopyMacosBridge && ./gradlew run` as a workaround
        val resourcesDir = File(System.getProperty("compose.application.resources.dir"))
        val libraryPath = resourcesDir
            .resolve("libs/${System.mapLibraryName(libraryName)}")
            .absolutePath

        try {
            System.load(libraryPath)
        } catch (e: UnsatisfiedLinkError) {
            error("Could not load $libraryName: ${e.message}")
        }
    } else error("Platform `$osName` not supported !")
}
