/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.dialog

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

actual fun createDefaultFileDialog(): FileDialog = DefaultFileDialog

private object DefaultFileDialog: FileDialog {
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
