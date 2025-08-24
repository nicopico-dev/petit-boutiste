package fr.nicopico.petitboutiste.utils.file

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import java.io.File

suspend fun showFileDialog(
    operation: FileDialogOperation,
    title: String? = null,
    block: (File) -> Unit,
) {
    when (operation) {
        is FileDialogOperation.ChooseFile -> {
            val selectedFile = FileKit.openFilePicker(
                title = title,
                type = FileKitType.File(operation.extensions),
            ) ?: return
            block(selectedFile.file)
        }

        is FileDialogOperation.ChooseFolder -> {
            val selectedFolder = FileKit.openDirectoryPicker(title)
                ?: return
            block(selectedFolder.file)
        }

        is FileDialogOperation.CreateNewFile -> {
            val newFile = FileKit.openFileSaver(
                suggestedName = operation.suggestedFilename,
                extension = operation.extension,
            ) ?: return
            block(newFile.file)
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
