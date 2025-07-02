package fr.nicopico.petitboutiste.ui.components.template

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun ExportTemplatesDialog(
    onDismissRequest: () -> Unit,
    onExport: (String) -> String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Export Templates") },
        text = {
            Text("This will export all templates to a JSON file. Click Export to select a file location.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Create a file chooser dialog
                    val fileChooser = JFileChooser().apply {
                        dialogTitle = "Save Templates"
                        fileSelectionMode = JFileChooser.FILES_ONLY
                        fileFilter = FileNameExtensionFilter("JSON Files", "json")
                        selectedFile = File("templates.json")
                    }

                    // Show the dialog and handle the result
                    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        val file = fileChooser.selectedFile
                        // Ensure the file has a .json extension
                        val filePath = if (!file.name.lowercase().endsWith(".json")) {
                            "${file.absolutePath}.json"
                        } else {
                            file.absolutePath
                        }

                        try {
                            // Export templates to JSON
                            val jsonData = onExport(filePath)
                            File(filePath).writeText(jsonData)
                            onDismissRequest()
                        } catch (e: Exception) {
                            // Handle export errors
                            println("Error exporting templates: ${e.message}")
                        }
                    } else {
                        // User cancelled the dialog
                        onDismissRequest()
                    }
                }
            ) {
                Text("Export")
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
