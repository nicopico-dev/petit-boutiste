package fr.nicopico.petitboutiste.ui.components.template.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun ImportTemplatesDialog(
    onDismissRequest: () -> Unit,
    onImport: (String, Boolean) -> Unit,
    initialReplaceState: Boolean = false
) {
    var importReplace by remember { mutableStateOf(initialReplaceState) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Import Templates") },
        text = {
            Column {
                Text("This will import templates from a JSON file.")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Checkbox(
                        checked = importReplace,
                        onCheckedChange = { importReplace = it }
                    )
                    Text("Replace existing templates", modifier = Modifier.padding(start = 8.dp))
                }
                Text(
                    "Click Import to select a file.",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Create a file chooser dialog
                    val fileChooser = JFileChooser().apply {
                        dialogTitle = "Import Templates"
                        fileSelectionMode = JFileChooser.FILES_ONLY
                        fileFilter = FileNameExtensionFilter("JSON Files", "json")
                    }

                    // Show the dialog and handle the result
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        val file = fileChooser.selectedFile

                        try {
                            // Import templates from JSON
                            val jsonData = file.readText()
                            onImport(jsonData, importReplace)
                        } catch (e: Exception) {
                            // Handle import errors
                            println("Error importing templates: ${e.message}")
                        }
                    } else {
                        // User cancelled the dialog
                        onDismissRequest()
                    }
                }
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
}
