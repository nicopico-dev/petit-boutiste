package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import fr.nicopico.petitboutiste.utils.compose.Slot
import fr.nicopico.petitboutiste.utils.compose.optionalSlot
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSelector(
    onFileSelected: (File?) -> Unit,
    modifier: Modifier = Modifier,
    selection: File? = null,
    label: Slot? = null,
) {
    val pickerLauncher = rememberFilePickerLauncher { selectedFile ->
        if (selectedFile != null) {
            onFileSelected(selectedFile.file)
        }
    }

    val defaultColors = TextFieldDefaults.colors()
    OutlinedTextField(
        value = selection?.name ?: "",
        onValueChange = { /* no-op for read-only display */ },
        label = label,
        trailingIcon = { Icon(Icons.Outlined.FolderOpen, null) },
        supportingText = selection?.optionalSlot { Text(it.parent) },
        enabled = false,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            disabledTextColor = defaultColors.unfocusedTextColor,
            disabledLabelColor = defaultColors.unfocusedLabelColor,
            disabledContainerColor = Color.Transparent,
            disabledIndicatorColor = defaultColors.unfocusedIndicatorColor,
            disabledPlaceholderColor = defaultColors.unfocusedPlaceholderColor,
            disabledTrailingIconColor = defaultColors.unfocusedTrailingIconColor,
            disabledSupportingTextColor = defaultColors.unfocusedSupportingTextColor,
        ),
        modifier = modifier.clickable {
            pickerLauncher.launch()
        },
    )
}

@Preview
@Composable
private fun FileSelectorPreview() {
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FileSelector(
                onFileSelected = { }
            )

            FileSelector(
                selection = File("/tmp/example.txt"),
                onFileSelected = { }
            )

            FileSelector(
                label = { Text("Choose a file") },
                onFileSelected = { }
            )

            FileSelector(
                label = { Text("Choose a file") },
                selection = File("/tmp/example.txt"),
                onFileSelected = { }
            )
        }
    }
}
