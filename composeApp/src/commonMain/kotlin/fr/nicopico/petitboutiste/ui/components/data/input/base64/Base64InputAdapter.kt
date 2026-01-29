/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.data.input.base64

import fr.nicopico.petitboutiste.models.data.Base64String
import fr.nicopico.petitboutiste.ui.components.data.input.DataInputAdapter

object Base64InputAdapter : DataInputAdapter<Base64String> {

    override val placeholder = "Paste BASE64 string here"

    override fun parse(input: String) = Base64String.parse(input)

    override fun getNormalizedString(value: Base64String): String = value.base64String
}
