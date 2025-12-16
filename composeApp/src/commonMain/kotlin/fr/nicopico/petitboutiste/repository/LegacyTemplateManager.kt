/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
