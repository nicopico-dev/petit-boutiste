/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.input.hex

import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.components.input.DataInputAdapter

object HexInputAdapter : DataInputAdapter<HexString> {

    override val placeholder = "Paste hexadecimal string here (e.g., 48656C6C6F)"

    override fun parse(input: String) = HexString.parse(input)

    override fun getNormalizedString(value: HexString): String = value.hexString
}
