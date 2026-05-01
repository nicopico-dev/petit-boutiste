/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.data.toByteItems
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.files.Path
import java.util.UUID

/**
 * Represents a unique identifier for a tab
 */
@JvmInline
@Immutable
value class TabId(val value: String) {
    companion object {
        fun create(): TabId = TabId(UUID.randomUUID().toString())
    }
}

/**
 * Represents the data for a single tab, including its input data, input type, and group definitions
 */
@Stable
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

@Stable
data class TabDataRendering(
    val inputData: DataString = HexString(""),
    val groupDefinitions: List<ByteGroupDefinition> = emptyList(),
) {
    private var byteItems: List<ByteItem>? = null
    private val byteItemsMutex = Mutex()

    var isRendered by mutableStateOf(false)
        private set

    @Suppress("ReturnCount")
    suspend fun renderByteItems(): List<ByteItem> {
        // Fast path
        byteItems?.let { return it }

        // Slow path
        return byteItemsMutex.withLock {
            // double-check
            byteItems?.let { return it }

            val result = inputData.toByteItems(groupDefinitions)
            byteItems = result
            isRendered = true
            result
        }
    }
}

@Immutable
data class TabTemplateData(
    val templateFilePath: Path,
    val definitionsHaveChanged: Boolean = false,
)
