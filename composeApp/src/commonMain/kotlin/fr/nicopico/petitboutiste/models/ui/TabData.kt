package fr.nicopico.petitboutiste.models.ui

import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.input.DataString
import fr.nicopico.petitboutiste.models.input.HexString
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
    val name: String = "Untitled",
    val inputData: DataString = HexString(""),
    val inputType: InputType = InputType.HEX,
    val groupDefinitions: List<ByteGroupDefinition> = emptyList()
)
