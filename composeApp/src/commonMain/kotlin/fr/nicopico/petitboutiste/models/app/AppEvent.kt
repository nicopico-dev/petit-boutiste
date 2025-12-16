/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.app

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.TabId
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import java.io.File

sealed class AppEvent {
    data class AddNewTabEvent(val tabData: TabData? = null) : AppEvent()

    data class SelectTabEvent(val tabId: TabId) : AppEvent()
    data class RenameTabEvent(val tabId: TabId, val tabName: String) : AppEvent()
    data class RemoveTabEvent(val tabId: TabId) : AppEvent()
    data class DuplicateTabEvent(val tabId: TabId) : AppEvent()
    data class CycleTabEvent(val cycleForward: Boolean) : AppEvent()
    data class SwitchAppThemeEvent(val appTheme: PBTheme) : AppEvent()

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

        data class LoadTemplateEvent(
            val templateFile: File,
            val definitionsOnly: Boolean,
        ) : CurrentTabEvent()
        data class SaveTemplateEvent(val templateFile: File, val updateExisting: Boolean) : CurrentTabEvent()
        data class AddDefinitionsFromTemplateEvent(val templateFile: File) : CurrentTabEvent()
    }

    data class ExportLegacyTemplatesEvent(val exportFolder: File): AppEvent()
    data class ConvertLegacyTemplatesBundleEvent(val bundleFile: File, val exportFolder: File): AppEvent()
    data object ClearAllLegacyTemplates : AppEvent()
}
