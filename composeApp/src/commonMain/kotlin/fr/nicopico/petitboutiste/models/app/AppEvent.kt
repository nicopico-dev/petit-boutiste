package fr.nicopico.petitboutiste.models.app

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.models.ui.TabId

sealed class AppEvent {
    data object AddNewTabEvent : AppEvent()
    data class SelectTabEvent(val tabId: TabId) : AppEvent()
    data class RemoveTabEvent(val tabId: TabId) : AppEvent()
    data class RenameTabEvent(val tabId: TabId, val tabName: String) : AppEvent()

    sealed class CurrentTabEvent : AppEvent() {
        data class ChangeInputTypeEvent(val type: InputType) : CurrentTabEvent()
        data class ChangeInputDataEvent(val data: DataString) : CurrentTabEvent()
        data class ChangeDefinitionsEvent(val definitions: List<ByteGroupDefinition>): CurrentTabEvent()
    }
}
