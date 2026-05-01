/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.file

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

private val fileSystem = SystemFileSystem

fun Path.asString(): String = toString()

val Path.nameWithoutExtension: String
    get() = name.substringBeforeLast(".")

expect val Path.absolutePath: String

expect fun Path.lastModified(): Long

fun Path.exists(): Boolean = fileSystem.exists(this)

fun Path.asSource(): Source = fileSystem.source(this).buffered()
