package fr.nicopico.petitboutiste.ui.infra.savers

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.TabData
import fr.nicopico.petitboutiste.models.TabId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Serializable data class for saving tab data
 */
@Serializable
private data class SerializableTabData(
    val id: String,
    val name: String,
    val inputData: String,
    val groupDefinitions: String
)

/**
 * Saver for a list of [TabData] objects
 */
val TabsSaver = object : Saver<List<TabData>, String> {
    override fun SaverScope.save(value: List<TabData>): String {
        val serializableTabs = value.map { tabData ->
            val groupDefinitionsJson = with(GroupDefinitionsSaver) {
                save(tabData.groupDefinitions) ?: ""
            }

            SerializableTabData(
                id = tabData.id.value,
                name = tabData.name,
                inputData = tabData.inputData.hexString,
                groupDefinitions = groupDefinitionsJson
            )
        }

        return Json.encodeToString(serializableTabs)
    }

    override fun restore(value: String): List<TabData> {
        val serializableTabs = Json.decodeFromString<List<SerializableTabData>>(value)

        return serializableTabs.map { serializedTab ->
            val groupDefinitions = if (serializedTab.groupDefinitions.isNotEmpty()) {
                GroupDefinitionsSaver.restore(serializedTab.groupDefinitions) ?: emptyList()
            } else {
                emptyList()
            }

            TabData(
                id = TabId(serializedTab.id),
                name = serializedTab.name,
                inputData = HexString(serializedTab.inputData),
                groupDefinitions = groupDefinitions
            )
        }
    }
}

/**
 * Saver for a [TabId]
 */
val TabIdSaver = object : Saver<TabId, String> {
    override fun SaverScope.save(value: TabId): String {
        return value.value
    }

    override fun restore(value: String): TabId {
        return TabId(value)
    }
}
