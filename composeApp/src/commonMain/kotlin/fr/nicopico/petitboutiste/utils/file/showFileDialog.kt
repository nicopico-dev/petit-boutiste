package fr.nicopico.petitboutiste.utils.file

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun showFileDialog(
    operation: FileDialogOperation,
    title: String? = null,
    block: (File) -> Unit,
) = withContext(Dispatchers.IO) {
    when (operation) {
        is FileDialogOperation.ChooseFile -> {
            val selectedFile = FileKit.openFilePicker(
                title = title,
                type = FileKitType.File(operation.extensions),
            ) ?: return@withContext
            block(selectedFile.file)
        }

        is FileDialogOperation.ChooseFolder -> {
            val selectedFolder = FileKit.openDirectoryPicker(title)
                ?: return@withContext
            block(selectedFolder.file)
        }

        is FileDialogOperation.CreateNewFile -> {
            val newFile = FileKit.openFileSaver(
                suggestedName = operation.suggestedFilename,
                extension = operation.extension,
            ) ?: return@withContext
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
