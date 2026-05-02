/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.file

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlin.uuid.Uuid

fun Path.asString(): String = toString()

val Path.nameWithoutExtension: String
    get() = name.substringBeforeLast(".")

val Path.parentOrCurrent: Path
    get() = parent ?: Path(".")

expect val Path.absolutePath: String

expect fun Path.lastModified(): Long

fun Path.exists(
    fileSystem: FileSystem = SystemFileSystem
): Boolean = fileSystem.exists(this)

fun Path.asSource(
    fileSystem: FileSystem = SystemFileSystem
): Source = fileSystem.source(this).buffered()

fun Path.asSink(
    fileSystem: FileSystem = SystemFileSystem,
    append: Boolean = false,
): Sink = fileSystem.sink(this, append).buffered()

fun createTempFile(
    fileSystem: FileSystem = SystemFileSystem,
    directory: Path = SystemTemporaryDirectory,
): Path {
    val tempFile = Path(directory, "tmp-${Uuid.random()}")
    if (!fileSystem.exists(tempFile)) {
        SystemFileSystem.sink(tempFile).close()
        return tempFile
    } else error("Could not create temporary file in $directory")
}

/**
 * Normalizes the current path by resolving `.` and `..` segments and converting
 * all backslashes to forward slashes. This ensures the path is in a canonical form.
 *
 * The normalization process removes redundant components such as `.` and handles
 * `..` by effectively navigating to the parent directory when possible.
 * Any leading or trailing slashes are preserved in the normalized result.
 *
 * @return The normalized path.
 */
fun Path.normalize(): Path {
    val pathString = toString().replace('\\', '/')
    val isAbsolute = pathString.startsWith('/')
    val parts = pathString
        .split('/')
        .fold(mutableListOf<String>()) { normalizedParts, part ->
            when (part) {
                "", "." -> Unit
                ".." -> {
                    if (normalizedParts.isNotEmpty() && normalizedParts.last() != "..") {
                        normalizedParts.removeLast()
                    } else if (!isAbsolute) {
                        normalizedParts += part
                    }
                }
                else -> normalizedParts += part
            }
            normalizedParts
        }

    val result = parts.joinToString("/")
    return Path(if (isAbsolute) "/$result" else result)
}

/**
 * Computes the relative path from the specified base directory to the current path.
 *
 * The method normalizes both the current path and the base directory path. It determines
 * the common prefix between the two paths and calculates the necessary relative path
 * components to navigate from the base directory to the current path.
 *
 * @param baseDir The base directory from which the relative path should be calculated.
 * @return The relative path from the base directory to the current path as a string.
 */
fun Path.relativeTo(
    baseDir: Path,
): String {
    val normalizedPath = normalize()
    val normalizedBase = baseDir.normalize()

    val pathIsAbsolute = normalizedPath.toString().startsWith('/')
    val baseIsAbsolute = normalizedBase.toString().startsWith('/')

    if (pathIsAbsolute != baseIsAbsolute) {
        return normalizedPath.toString()
    }

    val normalizedPathParts = normalizedPath.toString().splitPath()
    val normalizedBaseParts = normalizedBase.toString().splitPath()

    val commonPrefixLength = normalizedPathParts
        .zip(normalizedBaseParts)
        .takeWhile { (pathPart, basePart) -> pathPart == basePart }
        .size

    val parentParts = List(normalizedBaseParts.size - commonPrefixLength) { ".." }
    val childParts = normalizedPathParts.drop(commonPrefixLength)

    val resultParts = parentParts + childParts
    return if (resultParts.isEmpty()) "." else resultParts.joinToString("/")
}

private fun String.splitPath(): List<String> =
    replace('\\', '/')
        .split('/')
        .filter { it.isNotEmpty() }
