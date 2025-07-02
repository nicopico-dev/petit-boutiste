package fr.nicopico.petitboutiste.ui.components.template

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun LoadTemplateDialog(
    onDismissRequest: () -> Unit,
    onTemplateLoaded: (List<ByteGroupDefinition>) -> Unit,
    onTemplateDeleted: (Uuid) -> Unit,
    templates: List<Template>,
    initialSelectedTemplate: Template? = null
) {
    var selectedTemplate by remember { mutableStateOf(initialSelectedTemplate) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
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
                            onTemplateDeleted(template.id)
                            selectedTemplate = null
                        }
                    },
                    enabled = selectedTemplate != null
                ) {
                    Text("Delete")
                }
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}
