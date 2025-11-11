package fr.nicopico.petitboutiste.ui.components.input.hex

import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.components.input.DataInputAdapter

object HexInputAdapter : DataInputAdapter<HexString> {

    override val placeholder = "Paste hexadecimal string here (e.g., 48656C6C6F)"

    override fun parse(input: String) = HexString.parse(input)

    override fun getNormalizedString(value: HexString): String = value.hexString
}
