package fr.nicopico.petitboutiste.ui.components.template

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.Template
import fr.nicopico.petitboutiste.repository.TemplateRepository
import fr.nicopico.petitboutiste.ui.components.foundation.IconButtonWithLabel
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TemplateManagement(
    modifier: Modifier = Modifier,
    definitions: List<ByteGroupDefinition> = emptyList(),
    onTemplateLoaded: (List<ByteGroupDefinition>) -> Unit = {},
) {
    val templateRepository = remember { TemplateRepository() }
    val templates by templateRepository.observe().collectAsState(emptyList())

    var collapsed by remember {
        mutableStateOf(false)
    }

    var showSaveDialog by remember {
        mutableStateOf(false)
    }

    var showLoadDialog by remember {
        mutableStateOf(false)
    }

    var showClearDialog by remember {
        mutableStateOf(false)
    }

    var showExportDialog by remember {
        mutableStateOf(false)
    }

    var showImportDialog by remember {
        mutableStateOf(false)
    }

    var templateName by remember {
        mutableStateOf("")
    }

    var selectedTemplate by remember {
        mutableStateOf<Template?>(null)
    }

    var importReplace by remember {
        mutableStateOf(false)
    }

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.clickable {
                collapsed = !collapsed
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Template", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))

            Icon(
                if (collapsed) Icons.Default.UnfoldMore else Icons.Default.UnfoldLess,
                "Toggle",
                modifier = Modifier.size(18.dp)
            )
        }
        if (!collapsed) {
            Row(
                Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButtonWithLabel(
                    icon = Icons.Outlined.FileOpen,
                    label = "Load",
                    onClick = {
                        selectedTemplate = null
                        showLoadDialog = true
                    },
                )
                IconButtonWithLabel(
                    icon = Icons.Outlined.Save,
                    label = "Save",
                    onClick = {
                        templateName = ""
                        showSaveDialog = true
                    },
                )
                IconButtonWithLabel(
                    icon = Icons.Outlined.Upload,
                    label = "Export",
                    onClick = {
                        showExportDialog = true
                    },
                )
                IconButtonWithLabel(
                    icon = Icons.Outlined.Download,
                    label = "Import",
                    onClick = {
                        importReplace = false
                        showImportDialog = true
                    },
                )
                IconButtonWithLabel(
                    icon = Icons.Outlined.Clear,
                    label = "Clear",
                    onClick = {
                        showClearDialog = true
                    },
                )
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Template") },
            text = {
                Column {
                    Text("Enter a name for your template:")
                    OutlinedTextField(
                        value = templateName,
                        onValueChange = { templateName = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (templateName.isNotBlank()) {
                            // Format current date/time in ISO format
                            val timestamp = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                            )
                            // Add timestamp to template name
                            val nameWithTimestamp = "$templateName ($timestamp)"

                            val template = Template(
                                name = nameWithTimestamp,
                                definitions = definitions
                            )
                            templateRepository.save(template)
                            showSaveDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLoadDialog) {
        AlertDialog(
            onDismissRequest = { showLoadDialog = false },
            title = { Text("Load Template") },
            text = {
                Column {
                    Text("Select a template to load:")
                    if (templates.isEmpty()) {
                        Text("No templates available", modifier = Modifier.padding(top = 8.dp))
                    } else {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            templates
                                .sortedBy(Template::name)
                                .forEach { template ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTemplate = template }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = template.name,
                                            fontWeight = if (selectedTemplate?.id == template.id) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTemplate?.let { template ->
                            // Return the selected template's definitions
                            onTemplateLoaded(template.definitions)
                            showLoadDialog = false
                        }
                    },
                    enabled = selectedTemplate != null
                ) {
                    Text("Load")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            selectedTemplate?.let { template ->
                                templateRepository.delete(template.id)
                                selectedTemplate = null
                            }
                        },
                        enabled = selectedTemplate != null
                    ) {
                        Text("Delete")
                    }
                    TextButton(
                        onClick = { showLoadDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Definitions") },
            text = {
                Text("Are you sure you want to clear all definitions? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTemplateLoaded(emptyList())
                        showClearDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
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
                                val jsonData = templateRepository.exportToJson()
                                File(filePath).writeText(jsonData)
                                showExportDialog = false
                            } catch (e: Exception) {
                                // Handle export errors
                                println("Error exporting templates: ${e.message}")
                            }
                        } else {
                            // User cancelled the dialog
                            showExportDialog = false
                        }
                    }
                ) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExportDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Templates") },
            text = {
                Column {
                    Text("This will import templates from a JSON file.")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        androidx.compose.material3.Checkbox(
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
                                templateRepository.importFromJson(jsonData, importReplace)
                                showImportDialog = false
                            } catch (e: Exception) {
                                // Handle import errors
                                println("Error importing templates: ${e.message}")
                            }
                        } else {
                            // User cancelled the dialog
                            showImportDialog = false
                        }
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showImportDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
