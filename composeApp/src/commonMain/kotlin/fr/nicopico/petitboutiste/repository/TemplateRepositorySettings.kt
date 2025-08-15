package fr.nicopico.petitboutiste.repository

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.nullableString
import fr.nicopico.petitboutiste.models.Template
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors
import java.util.prefs.Preferences
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TemplateRepositorySettings(
    settings: Settings = PreferencesSettings(
        Preferences.userNodeForPackage(TemplateRepository::class.java)
    ),
    private val json: Json = Json.Default,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
) : TemplateRepository {

    // Use a single-thread dispatcher for serialization
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private var jsonData by settings.nullableString("TEMPLATES")
    private val templates = MutableStateFlow<List<Template>>(emptyList())

    init {
        scope.launch {
            withContext(dispatcher) {
                val loadedTemplates = jsonData?.let { jsonData ->
                    Json.decodeFromString<List<Template>>(jsonData)
                } ?: emptyList()
                templates.emit(loadedTemplates)
            }
        }
    }

    override fun observe(): Flow<List<Template>> {
        return templates
    }

    override fun save(template: Template) {
        val newValue = templates
            .updateAndGet { existingTemplates ->
                val index = existingTemplates.indexOfFirst { it.id == template.id }
                if (index == -1) {
                    existingTemplates + template
                } else {
                    existingTemplates.map {
                        if (it.id == template.id) {
                            template
                        } else it
                    }
                }
            }
        scope.launch {
            withContext(dispatcher) {
                jsonData = json.encodeToString(newValue)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun delete(templateId: Uuid) {
        val newValue = templates
            .updateAndGet { existingTemplates ->
                existingTemplates.filter { it.id != templateId }
            }
        scope.launch {
            withContext(dispatcher) {
                jsonData = json.encodeToString(newValue)
            }
        }
    }

    override fun exportToJson(): String {
        return json.encodeToString(templates.value)
    }

    override fun importFromJson(json: String, replace: Boolean) {
        try {
            val importedTemplates = Json.decodeFromString<List<Template>>(json)
            val newValue = templates.updateAndGet { existingTemplates ->
                if (replace) {
                    importedTemplates
                } else {
                    // Merge templates, keeping existing ones if there's an ID conflict
                    val existingIds = existingTemplates.map { it.id }.toSet()
                    val newTemplates = importedTemplates.filter { it.id !in existingIds }
                    existingTemplates + newTemplates
                }
            }
            scope.launch {
                withContext(dispatcher) {
                    jsonData = this@TemplateRepositorySettings.json.encodeToString(newValue)
                }
            }
        } catch (e: Exception) {
            // Handle JSON parsing errors
            println("Error importing templates: ${e.message}")
        }
    }
}
