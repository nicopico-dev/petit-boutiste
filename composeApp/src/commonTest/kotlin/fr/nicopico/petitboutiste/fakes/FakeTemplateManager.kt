/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.fakes

import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.repository.TemplateManager
import kotlinx.io.files.Path

class FakeTemplateManager : TemplateManager {
    override suspend fun loadTemplate(templateFilePath: Path): Template = Template(name = "Default")
    override suspend fun saveTemplate(template: Template, templateFilePath: Path, overwrite: Boolean) {}
}
