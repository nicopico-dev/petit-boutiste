/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop
import fr.nicopico.petitboutiste.utils.file.FileDialog
import fr.nicopico.petitboutiste.utils.file.FileDialogOperation
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.typography
import java.io.File

@Composable
fun PBFileSelector(
    onFileSelected: (File?) -> Unit,
    modifier: Modifier = Modifier,
    selection: File? = null,
    fileDialog: FileDialog = FileDialog.Default,
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            val state = remember(selection) {
                TextFieldState(selection?.name ?: "")
            }
            TextField(
                state = state,
                readOnly = true,
                modifier = Modifier.weight(1f),
                placeholder = { Text("No file selected") },
            )

            OutlinedButton(
                content = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Select")

                        Icon(
                            key = AllIconsKeys.General.OpenDisk,
                            contentDescription = null
                        )
                    }
                },
                onClick = {
                    coroutineScope.launch {
                        fileDialog.show(FileDialogOperation.ChooseFile()) { file ->
                            onFileSelected(file)
                        }
                    }
                },
            )
        }

        if (selection != null) {
            Text(
                selection.parent,
                style = JewelTheme.typography.small,
                color = AppTheme.current.colors.subTextColor,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

private object FileSelectionParameterProvider : PreviewParameterProvider<File?> {
    override val values: Sequence<File?> = sequenceOf(
        null,
        File("/path/to/example.txt"),
    )
}

@Preview
@Composable
private fun PBFileSelectorPreview() {
    WrapForPreviewDesktop(FileSelectionParameterProvider) {
        PBFileSelector(
            modifier = Modifier.padding(8.dp),
            onFileSelected = {},
            selection = it,
        )
    }
}
