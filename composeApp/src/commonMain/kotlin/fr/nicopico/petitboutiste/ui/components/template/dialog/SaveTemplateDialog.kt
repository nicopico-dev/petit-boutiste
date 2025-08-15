package fr.nicopico.petitboutiste.ui.components.template.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.Template
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun SaveTemplateDialog(
    onDismissRequest: () -> Unit,
    onSave: (Template) -> Unit,
    definitions: List<ByteGroupDefinition>,
    initialTemplateName: String = "",
) {
    var templateName by remember { mutableStateOf(initialTemplateName) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
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
                        onSave(template)
                    }
                }
            ) {
                Text("Save")
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
