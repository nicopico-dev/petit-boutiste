package fr.nicopico.petitboutiste.ui.components.input.hex

import fr.nicopico.petitboutiste.models.input.HexString
import fr.nicopico.petitboutiste.ui.components.input.DataInputAdapter

object HexInputAdapter : DataInputAdapter<HexString> {
    override fun toText(value: HexString) = value.hexString

    override fun isReady(input: String) = input.length % 2 == 0

    override fun isValid(input: String): Boolean {
        return input.all { it.isDigit() || it.uppercaseChar() in 'A'..'F' }
    }

    override fun parse(input: String) = HexString.parse(input)

    override val placeholder = "Paste hexadecimal string here (e.g., 48656C6C6F)"
}
