/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.persistence.Template
import kotlinx.io.files.Path

interface TemplateManager {

    suspend fun loadTemplate(templateFilePath: Path): Template

    suspend fun saveTemplate(
        template: Template,
        templateFilePath: Path,
        overwrite: Boolean = false,
    )

    companion object {
        private val instance: TemplateManager by lazy {
            TemplateManagerImpl()
        }

        operator fun invoke(): TemplateManager = instance
    }
}
