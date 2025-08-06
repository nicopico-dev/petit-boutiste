package fr.nicopico.petitboutiste.models

/**
 * Represents a unique identifier for a tab
 */
@JvmInline
value class TabId(val value: String) {
    companion object {
        fun create(): TabId = TabId(java.util.UUID.randomUUID().toString())
    }
}

/**
 * Represents the data for a single tab, including its input data and group definitions
 */
data class TabData(
    val id: TabId = TabId.create(),
    val name: String = "Untitled",
    val inputData: HexString = HexString(""),
    val groupDefinitions: List<ByteGroupDefinition> = emptyList()
)
