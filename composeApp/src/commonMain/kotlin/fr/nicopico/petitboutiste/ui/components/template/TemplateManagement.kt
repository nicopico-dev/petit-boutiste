package fr.nicopico.petitboutiste.ui.components.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.Template
import fr.nicopico.petitboutiste.repository.TemplateRepository
import fr.nicopico.petitboutiste.ui.components.foundation.Collapsable
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

    var templateName by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf<Template?>(null) }

    // Single dialog state to ensure only one dialog is displayed at a time
    var dialogState by remember { mutableStateOf(TemplateDialogState.None) }

    var importReplace by remember { mutableStateOf(false) }

    Collapsable(
        title = "Template",
        modifier = modifier.fillMaxWidth(),
        initialCollapsed = false,
    ) {
        Row(
            Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButtonWithLabel(
                icon = Icons.Outlined.FileOpen,
                label = "Load",
                onClick = {
                    selectedTemplate = null
                    dialogState = TemplateDialogState.Load
                },
            )
            IconButtonWithLabel(
                icon = Icons.Outlined.Save,
                label = "Save",
                onClick = {
                    templateName = ""
                    dialogState = TemplateDialogState.Save
                },
            )
            IconButtonWithLabel(
                icon = Icons.Outlined.Upload,
                label = "Export",
                onClick = {
                    dialogState = TemplateDialogState.Export
                },
            )
            IconButtonWithLabel(
                icon = Icons.Outlined.Download,
                label = "Import",
                onClick = {
                    importReplace = false
                    dialogState = TemplateDialogState.Import
                },
            )
            IconButtonWithLabel(
                icon = Icons.Outlined.Clear,
                label = "Clear",
                onClick = {
                    dialogState = TemplateDialogState.Clear
                },
            )
        }
    }

    // Display the appropriate dialog based on the current dialog state
    when (dialogState) {
        TemplateDialogState.Save -> {
            SaveTemplateDialog(
                onDismissRequest = { dialogState = TemplateDialogState.None },
                onSave = { template ->
                    templateRepository.save(template)
                    dialogState = TemplateDialogState.None
                },
                definitions = definitions,
                initialTemplateName = templateName
            )
        }
        TemplateDialogState.Load -> {
            LoadTemplateDialog(
                onDismissRequest = { dialogState = TemplateDialogState.None },
                onTemplateLoaded = { loadedDefinitions ->
                    onTemplateLoaded(loadedDefinitions)
                    dialogState = TemplateDialogState.None
                },
                onTemplateDeleted = { templateId ->
                    templateRepository.delete(templateId)
                    selectedTemplate = null
                },
                templates = templates,
                initialSelectedTemplate = selectedTemplate
            )
        }
        TemplateDialogState.Clear -> {
            ClearDefinitionsDialog(
                onDismissRequest = { dialogState = TemplateDialogState.None },
                onConfirm = {
                    onTemplateLoaded(emptyList())
                    dialogState = TemplateDialogState.None
                }
            )
        }
        TemplateDialogState.Export -> {
            ExportTemplatesDialog(
                onDismissRequest = { dialogState = TemplateDialogState.None },
                onExport = { _ ->
                    val jsonData = templateRepository.exportToJson()
                    dialogState = TemplateDialogState.None
                    jsonData
                }
            )
        }
        TemplateDialogState.Import -> {
            ImportTemplatesDialog(
                onDismissRequest = { dialogState = TemplateDialogState.None },
                onImport = { jsonData, replace ->
                    templateRepository.importFromJson(jsonData, replace)
                    dialogState = TemplateDialogState.None
                },
                initialReplaceState = importReplace
            )
        }
        TemplateDialogState.None -> {
            // No dialog to display
        }
    }
}
