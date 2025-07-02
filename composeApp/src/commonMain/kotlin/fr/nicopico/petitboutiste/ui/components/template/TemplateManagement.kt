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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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

    var collapsed by remember { mutableStateOf(false) }

    var templateName by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf<Template?>(null) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    var importReplace by remember { mutableStateOf(false) }

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
        SaveTemplateDialog(
            onDismissRequest = { showSaveDialog = false },
            onSave = { template ->
                templateRepository.save(template)
                showSaveDialog = false
            },
            definitions = definitions,
            initialTemplateName = templateName
        )
    }

    if (showLoadDialog) {
        LoadTemplateDialog(
            onDismissRequest = { showLoadDialog = false },
            onTemplateLoaded = { loadedDefinitions ->
                onTemplateLoaded(loadedDefinitions)
                showLoadDialog = false
            },
            onTemplateDeleted = { templateId ->
                templateRepository.delete(templateId)
                selectedTemplate = null
            },
            templates = templates,
            initialSelectedTemplate = selectedTemplate
        )
    }

    if (showClearDialog) {
        ClearDefinitionsDialog(
            onDismissRequest = { showClearDialog = false },
            onConfirm = {
                onTemplateLoaded(emptyList())
                showClearDialog = false
            }
        )
    }

    if (showExportDialog) {
        ExportTemplatesDialog(
            onDismissRequest = { showExportDialog = false },
            onExport = { _ ->
                val jsonData = templateRepository.exportToJson()
                showExportDialog = false
                jsonData
            }
        )
    }

    if (showImportDialog) {
        ImportTemplatesDialog(
            onDismissRequest = { showImportDialog = false },
            onImport = { jsonData, replace ->
                templateRepository.importFromJson(jsonData, replace)
                showImportDialog = false
            },
            initialReplaceState = importReplace
        )
    }
}
