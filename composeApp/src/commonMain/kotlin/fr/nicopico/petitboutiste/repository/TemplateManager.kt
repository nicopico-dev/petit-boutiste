package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.persistence.Template
import java.io.File

interface TemplateManager {

    suspend fun loadTemplate(templateFile: File): Template

    suspend fun saveTemplate(
        template: Template,
        templateFile: File,
        overwrite: Boolean = false,
    )

    companion object {
        private val instance: TemplateManager by lazy {
            TemplateManagerImpl()
        }

        operator fun invoke(): TemplateManager = instance
    }
}
