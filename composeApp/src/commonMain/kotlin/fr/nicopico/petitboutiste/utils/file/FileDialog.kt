/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.file

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.toKotlinxIoPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.files.Path

interface FileDialog {
    suspend fun show(
        operation: FileDialogOperation,
        title: String? = null,
        block: (Path) -> Unit,
    )

    companion object {
        val Default: FileDialog = FileDialogDefault
    }
}

private object FileDialogDefault: FileDialog {
    override suspend fun show(
        operation: FileDialogOperation,
        title: String?,
        block: (Path) -> Unit,
    ) = withContext(Dispatchers.IO) {
        when (operation) {
            is FileDialogOperation.ChooseFile -> {
                val selectedFile = FileKit.openFilePicker(
                    type = FileKitType.File(operation.extensions),
                    dialogSettings = FileKitDialogSettings(
                        title = title,
                    ),
                ) ?: return@withContext
                block(selectedFile.toKotlinxIoPath())
            }

            is FileDialogOperation.ChooseFolder -> {
                val selectedFolder = FileKit.openDirectoryPicker(
                    directory = null,
                    dialogSettings = FileKitDialogSettings(
                        title = title,
                    ),
                ) ?: return@withContext
                block(selectedFolder.toKotlinxIoPath())
            }

            is FileDialogOperation.CreateNewFile -> {
                val newFile = FileKit.openFileSaver(
                    suggestedName = operation.suggestedFilename,
                    extension = operation.extension,
                ) ?: return@withContext
                block(newFile.toKotlinxIoPath())
            }
        }
    }
}

sealed class FileDialogOperation {
    data object ChooseFolder : FileDialogOperation()

    data class ChooseFile(
        val extensions: Set<String>? = null
    ) : FileDialogOperation() {
        constructor(vararg extensions: String) : this(extensions.toSet())
    }

    data class CreateNewFile(
        val suggestedFilename: String,
        val extension: String,
    ) : FileDialogOperation()
}
