/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.data.toByteItems
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import java.io.File
import java.util.UUID

/**
 * Represents a unique identifier for a tab
 */
@JvmInline
value class TabId(val value: String) {
    companion object {
        fun create(): TabId = TabId(UUID.randomUUID().toString())
    }
}

/**
 * Represents the data for a single tab, including its input data, input type, and group definitions
 */
data class TabData(
    val id: TabId = TabId.create(),
    val name: String? = null,
    val rendering: TabDataRendering = TabDataRendering(),
    val scratchpad: String = "",
    val templateData: TabTemplateData? = null,
) {
    val inputData = rendering.inputData
    val groupDefinitions = rendering.groupDefinitions
    val isRendered: Boolean
        get() = rendering.isRendered

    suspend fun renderByteItems(): List<ByteItem> = rendering.renderByteItems()
}

data class TabDataRendering(
    val inputData: DataString = HexString(""),
    val groupDefinitions: List<ByteGroupDefinition> = emptyList(),
) {
    private var byteItems: List<ByteItem>? = null

    val isRendered: Boolean
        get() = byteItems != null

    suspend fun renderByteItems(): List<ByteItem> {
        // TODO Critical section!
        if (byteItems == null) {
            byteItems = inputData.toByteItems(groupDefinitions)
        }
        return byteItems!!
    }
}

data class TabTemplateData(
    val templateFile: File,
    val definitionsHaveChanged: Boolean = false,
)
