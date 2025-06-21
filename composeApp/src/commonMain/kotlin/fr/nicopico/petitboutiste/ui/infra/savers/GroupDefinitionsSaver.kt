package fr.nicopico.petitboutiste.ui.infra.savers

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import kotlinx.serialization.json.Json

object GroupDefinitionsSaver : Saver<List<ByteGroupDefinition>, String> {

    override fun SaverScope.save(value: List<ByteGroupDefinition>): String? {
        return Json.encodeToString(value)
    }

    override fun restore(value: String): List<ByteGroupDefinition>? {
        return Json.decodeFromString<List<ByteGroupDefinition>>(value)
    }
}
