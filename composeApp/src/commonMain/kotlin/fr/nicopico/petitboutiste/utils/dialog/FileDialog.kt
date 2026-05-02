/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.dialog

import kotlinx.io.files.Path

interface FileDialog {
    suspend fun show(
        operation: FileDialogOperation,
        title: String? = null,
        block: (Path) -> Unit,
    )

    companion object {
        val Default: FileDialog = createDefaultFileDialog()
    }
}

expect fun createDefaultFileDialog(): FileDialog

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
