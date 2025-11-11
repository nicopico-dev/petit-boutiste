package fr.nicopico.petitboutiste.ui.components.input.bin

import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.ui.components.input.DataInputAdapter

object BinaryInputAdapter : DataInputAdapter<BinaryString> {

    override val placeholder = "Paste binary string here (e.g., 01001000 )"

    override fun parse(input: String) = BinaryString.parse(input)

    override fun getNormalizedString(value: BinaryString): String = value.binaryString
}
