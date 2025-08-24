package fr.nicopico.petitboutiste.repository

import java.io.File

interface LegacyTemplateManager {

    suspend fun exportLegacyTemplates(outputFolder: File)

    suspend fun deleteAllLegacyTemplates()

    suspend fun convertLegacyTemplates(
        legacyTemplatesBundle: File,
        outputFolder: File,
    )

    companion object {
        private val instance: LegacyTemplateManager by lazy {
            LegacyTemplateManagerImpl(
                templateManager = TemplateManager(),
            )
        }

        operator fun invoke(): LegacyTemplateManager = instance
    }
}
