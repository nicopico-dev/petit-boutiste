package fr.nicopico.petitboutiste.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.AppEvent.CurrentTabEvent
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.utils.file.FileDialogOperation
import fr.nicopico.petitboutiste.utils.file.showFileDialog
import kotlinx.coroutines.launch

@Composable
fun FrameWindowScope.PBMenuBar(
    currentTab: TabData,
    onEvent: (AppEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()

    MenuBar {
        Menu("Template", mnemonic = 'T') {
            Item(
                text = "Load template",
                shortcut = KeyShortcut(Key.L, meta = true),
                onClick = {
                    if (currentTab.groupDefinitions.isNotEmpty()) {
                        // TODO Confirm overwrite
                    }
                    scope.launch {
                        showFileDialog(
                            title = "Load template",
                            operation = FileDialogOperation.ChooseFile("json")
                        ) { selectedFile ->
                            onEvent(CurrentTabEvent.LoadTemplateEvent(selectedFile))
                        }
                    }
                }
            )
            Item(
                text = "Save template",
                shortcut = KeyShortcut(Key.S, meta = true),
                onClick = {
                    if (currentTab.templateData != null) {
                        onEvent(
                            CurrentTabEvent.SaveTemplateEvent(
                                currentTab.templateData.templateFile,
                                updateExisting = true
                            )
                        )
                    } else {
                        scope.launch {
                            showFileDialog(
                                title = "Save new template",
                                operation = FileDialogOperation.CreateNewFile(
                                    suggestedFilename = currentTab.name ?: "Template",
                                    extension = "json",
                                ),
                            ) { selectedFile ->
                                onEvent(CurrentTabEvent.SaveTemplateEvent(selectedFile, updateExisting = false))
                            }
                        }
                    }
                }
            )
            Item(
                text = "Save template as ...",
                shortcut = KeyShortcut(Key.S, meta = true, shift = true),
                onClick = {
                    scope.launch {
                        showFileDialog(
                            title = "Save template as ...",
                            operation = FileDialogOperation.CreateNewFile(
                                suggestedFilename = currentTab.name ?: "Template",
                                extension = "json",
                            ),
                        ) { selectedFile ->
                            if (selectedFile.exists()) {
                                // TODO Confirm overwrite, otherwise exit
                            }
                            onEvent(CurrentTabEvent.SaveTemplateEvent(selectedFile, updateExisting = false))
                        }
                    }
                }
            )
        }

        Menu("Definitions", mnemonic = 'D') {
            Item(
                text = "Clear all definitions",
                icon = rememberVectorPainter(Icons.Outlined.Warning),
                onClick = {
                    // TODO Ask confirmation
                    onEvent(CurrentTabEvent.ClearAllDefinitionsEvent)
                }
            )

            Separator()

            Item(
                text = "Add definitions from template",
                onClick = {
                    scope.launch {
                        showFileDialog(
                            title = "Load definitions from...",
                            operation = FileDialogOperation.ChooseFile("json")
                        ) { selectedFile ->
                            onEvent(CurrentTabEvent.AddDefinitionsFromTemplateEvent(selectedFile))
                        }
                    }
                }
            )
        }

        Menu("Legacy templates") {
            Item(
                text = "Export legacy templates to folder",
                onClick = {
                    scope.launch {
                        showFileDialog(
                            title = "Select export folder for legacy templates",
                            operation = FileDialogOperation.ChooseFolder,
                        ) { selectedFolder ->
                            onEvent(AppEvent.ExportLegacyTemplatesEvent(selectedFolder))
                        }
                    }
                }
            )

            Item(
                text = "Convert legacy templates bundle to folder",
                onClick = {
                    scope.launch {
                        showFileDialog(
                            title = "Select legacy templates bundle to export",
                            operation = FileDialogOperation.ChooseFile("json"),
                        ) { selectedFile ->
                            val outputFolder = selectedFile.parentFile
                            onEvent(AppEvent.ConvertLegacyTemplatesBundleEvent(selectedFile, outputFolder))
                        }
                    }
                }
            )

            Separator()

            Item(
                text = "Clear all legacy templates",
                icon = rememberVectorPainter(Icons.Outlined.Warning),
                onClick = {
                    // TODO Ask confirmation
                    onEvent(AppEvent.ClearAllLegacyTemplates)
                }
            )
        }
    }
}
