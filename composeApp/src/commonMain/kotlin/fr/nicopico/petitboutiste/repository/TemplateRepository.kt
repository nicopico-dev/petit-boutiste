package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.Template
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface TemplateRepository {

    fun observe(): Flow<List<Template>>

    fun save(template: Template)

    @OptIn(ExperimentalUuidApi::class)
    fun delete(templateId: Uuid)

    /**
     * Export all templates to a JSON string
     */
    fun exportToJson(): String

    /**
     * Import templates from a JSON string
     * @param json The JSON string containing templates
     * @param replace Whether to replace existing templates or merge with them
     */
    fun importFromJson(json: String, replace: Boolean = false)

    companion object {
        private val instance by lazy {
            TemplateRepositorySettings()
        }

        operator fun invoke(): TemplateRepository = instance
    }
}
