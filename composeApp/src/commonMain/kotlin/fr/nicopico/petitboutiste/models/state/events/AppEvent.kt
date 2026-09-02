/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.state.events

import fr.nicopico.petitboutiste.models.InputType
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.state.TabData
import fr.nicopico.petitboutiste.models.state.TabDataRendering
import fr.nicopico.petitboutiste.models.state.TabId
import fr.nicopico.petitboutiste.models.state.TabTemplateData
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import kotlinx.io.files.Path

sealed class AppEvent {

    data object RefreshRenderingEvent : AppEvent()
    data class SwitchAppThemeEvent(
        val appTheme: PBTheme
    ) : AppEvent()

    sealed class TabManagementEvent : AppEvent()
    //region Tab management
    data class AddNewTabEvent(val tabData: TabData? = null) : TabManagementEvent()
    data class SelectTabEvent(val tabId: TabId) : TabManagementEvent()
    data class RenameTabEvent(val tabId: TabId, val tabName: String) : TabManagementEvent()
    data class RemoveTabEvent(val tabId: TabId) : TabManagementEvent()
    data class UndoRemoveTabEvent(val tabData: TabData, val index: Int) : TabManagementEvent()
    data class DuplicateTabEvent(val tabId: TabId) : TabManagementEvent()
    data class CycleTabEvent(val cycleForward: Boolean) : TabManagementEvent()
    data class OpenRenderedByteItemInNewTabEvent(
        val byteItem: ByteItem,
        val representation: Representation,
    ) : TabManagementEvent()
    //endregion

    sealed class CurrentTabEvent : AppEvent() {
        data class ChangeInputTypeEvent(val type: InputType) : CurrentTabEvent()
        data class ChangeInputDataEvent(val data: DataString) : CurrentTabEvent()

        data class AddDefinitionEvent(val definition: ByteGroupDefinition): CurrentTabEvent()
        data object AppendDefaultDefinitionEvent : CurrentTabEvent()
        data class DuplicateDefinitionEvent(val definition: ByteGroupDefinition) : CurrentTabEvent()
        data class UpdateDefinitionEvent(
            val sourceDefinition: ByteGroupDefinition,
            val updatedDefinition: ByteGroupDefinition,
        ): CurrentTabEvent()
        data class DeleteDefinitionEvent(val definition: ByteGroupDefinition): CurrentTabEvent()
        data object ClearAllDefinitionsEvent : CurrentTabEvent()
        data class UndoClearAllDefinitionsEvent(
            val tabId: TabId,
            val rendering: TabDataRendering,
            val templateData: TabTemplateData?,
        ) : CurrentTabEvent()

        data class UpdateScratchpadEvent(val scratchpad: String): CurrentTabEvent()
        data class UpdateDefaultRepresentationEvent(val representation: Representation): CurrentTabEvent()

        data class LoadTemplateEvent(
            val templateFilePath: Path,
            val definitionsOnly: Boolean,
        ) : CurrentTabEvent()
        data class SaveTemplateEvent(
            val templateFilePath: Path,
            val updateExisting: Boolean,
        ) : CurrentTabEvent()
        data class AddDefinitionsFromTemplateEvent(
            val templateFilePath: Path,
        ) : CurrentTabEvent()
    }
}
