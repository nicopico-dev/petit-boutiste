package fr.nicopico.petitboutiste.ui.components.template

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun TemplateManagement(
    modifier: Modifier = Modifier,
    definitions: List<ByteGroupDefinition> = emptyList(),
) {
    val templateRepository = remember { TemplateRepository() }
    val templates by templateRepository.observe().collectAsState(emptyList())

    var collapsed by remember {
        mutableStateOf(false)
    }

    var showSaveDialog by remember {
        mutableStateOf(false)
    }

    var templateName by remember {
        mutableStateOf("")
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
                Button(
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Load")
                            Icon(Icons.Default.FileOpen, null)
                        }
                    },
                    onClick = {
                        TODO("Handle load")
                    },
                )
                Button(
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Save")
                            Icon(Icons.Default.Save, null)
                        }
                    },
                    onClick = {
                        templateName = ""
                        showSaveDialog = true
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
                            val template = Template(
                                name = templateName,
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
}
