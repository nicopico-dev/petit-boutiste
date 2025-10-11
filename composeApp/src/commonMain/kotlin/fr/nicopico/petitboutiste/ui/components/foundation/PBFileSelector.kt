package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import fr.nicopico.petitboutiste.utils.file.FileDialogOperation
import fr.nicopico.petitboutiste.utils.file.showFileDialog
import fr.nicopico.petitboutiste.utils.preview.WrapForPreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
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
    label: String? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier) {
        if (label != null) {
            Text(label, style = JewelTheme.typography.medium)
            Spacer(Modifier.height(4.dp))
        }

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
                        showFileDialog(FileDialogOperation.ChooseFile()) { file ->
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
                color = JewelThemeUtils.colors.subTextColor,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PBFileSelectorPreview() {
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            PBFileSelector(
                onFileSelected = { }
            )

            PBFileSelector(
                selection = File("/tmp/example.txt"),
                onFileSelected = { }
            )

            PBFileSelector(
                label = "Choose a file",
                onFileSelected = { }
            )

            PBFileSelector(
                label = "Choose a file",
                selection = File("/tmp/example.txt"),
                onFileSelected = { }
            )
        }
    }
}
