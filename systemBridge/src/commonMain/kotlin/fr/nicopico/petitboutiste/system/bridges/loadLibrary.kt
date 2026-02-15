/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.system.bridges

import fr.nicopico.petitboutiste.system.exceptions.LoadingSystemBridgeLibraryException
import java.io.File

@Throws(LoadingSystemBridgeLibraryException::class)
internal fun loadNativeLibrary(libraryName: String) {
    // resourcesDir is null with tasks `desktopRun` and `desktopHotRun`, it only works for `run`
    //  https://github.com/JetBrains/compose-hot-reload/issues/343
    //  https://youtrack.jetbrains.com/issue/CMP-8800
    val resourceDirProperty = System.getProperty("compose.application.resources.dir")
        ?: throw LoadingSystemBridgeLibraryException("resourceDirProperty is not set")

    val resourcesDir = File(resourceDirProperty)
    val libraryPath = resourcesDir
        .resolve("libs/${System.mapLibraryName(libraryName)}")
        .absolutePath

    try {
        System.load(libraryPath)
    } catch (e: UnsatisfiedLinkError) {
        throw LoadingSystemBridgeLibraryException("Unable to load `$libraryName`", e)
    }
}
