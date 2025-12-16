/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.persistence

import fr.nicopico.petitboutiste.models.ui.TabData
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun TabData.toTemplate(
    fallbackName: String
) = Template(
    name = name ?: fallbackName,
    definitions = groupDefinitions,
)
