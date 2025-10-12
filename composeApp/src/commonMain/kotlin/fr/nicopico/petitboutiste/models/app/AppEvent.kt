package fr.nicopico.petitboutiste.models.app

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.models.ui.TabId
import java.io.File

sealed class AppEvent {
    data object AddNewTabEvent : AppEvent()
    data class SelectTabEvent(val tabId: TabId) : AppEvent()
    data class RenameTabEvent(val tabId: TabId, val tabName: String) : AppEvent()
    data class RemoveTabEvent(val tabId: TabId) : AppEvent()
    data class DuplicateTabEvent(val tabId: TabId) : AppEvent()
    data class CycleTabEvent(val cycleForward: Boolean) : AppEvent()

    sealed class CurrentTabEvent : AppEvent() {
        data class ChangeInputTypeEvent(val type: InputType) : CurrentTabEvent()
        data class ChangeInputDataEvent(val data: DataString) : CurrentTabEvent()
        data class AddDefinitionEvent(val definition: ByteGroupDefinition): CurrentTabEvent()
        data class UpdateDefinitionEvent(
            val sourceDefinition: ByteGroupDefinition,
            val updatedDefinition: ByteGroupDefinition,
        ): CurrentTabEvent()
        data class DeleteDefinitionEvent(val definition: ByteGroupDefinition): CurrentTabEvent()
        data object ClearAllDefinitionsEvent : CurrentTabEvent()
        data class UpdateScratchpadEvent(val scratchpad: String): CurrentTabEvent()

        data class LoadTemplateEvent(val templateFile: File) : CurrentTabEvent()
        data class SaveTemplateEvent(val templateFile: File, val updateExisting: Boolean) : CurrentTabEvent()
        data class AddDefinitionsFromTemplateEvent(val templateFile: File) : CurrentTabEvent()
    }

    data class ExportLegacyTemplatesEvent(val exportFolder: File): AppEvent()
    data class ConvertLegacyTemplatesBundleEvent(val bundleFile: File, val exportFolder: File): AppEvent()
    data object ClearAllLegacyTemplates : AppEvent()
}
