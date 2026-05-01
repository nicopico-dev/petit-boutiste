/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.file

import io.github.vinceglb.filekit.utils.toFile
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

private val fileSystem = SystemFileSystem

fun Path.asString(): String = toString()

val Path.nameWithoutExtension: String
    get() = name.substringBeforeLast(".")

val Path.absolutePath: String
    get() = this.toFile().absolutePath // `fileSystem.resolve(this).toString()` only works if the file exists

fun Path.lastModified(): Long = this.toFile().lastModified()

fun Path.exists(): Boolean = fileSystem.exists(this)

fun Path.asSource(): Source = fileSystem.source(this).buffered()
