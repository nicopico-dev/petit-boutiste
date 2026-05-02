/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.file

import io.github.vinceglb.filekit.utils.toFile
import kotlinx.io.files.Path

actual val Path.absolutePath: String
    get() = this.toFile().absolutePath // `fileSystem.resolve(this).toString()` only works if the file exists

actual fun Path.lastModified(): Long = this.toFile().lastModified()
