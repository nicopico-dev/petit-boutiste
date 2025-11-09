package fr.nicopico.petitboutiste.ui.components.input.bin

import fr.nicopico.petitboutiste.models.extensions.formatForDisplay
import fr.nicopico.petitboutiste.models.input.BinaryString
import fr.nicopico.petitboutiste.ui.components.input.DataInputAdapter

object BinaryInputAdapter : DataInputAdapter<BinaryString> {
    override fun toText(value: BinaryString) = value.binaryString

    override fun sanitize(input: String) = input.replace(" ", "")

    override fun formatForDisplay(input: String): String {
        return try {
            BinaryString(input).formatForDisplay()
        } catch (_: IllegalArgumentException) {
            input
        }
    }

    override fun isValid(input: String) = input.all { it == '0' || it == '1' }

    override fun isReady(input: String): Boolean {
        return input.isNotEmpty() && input.length % 8 == 0
    }

    override fun parse(input: String) = BinaryString.parse(input)

    override val placeholder = "Paste binary string here (e.g., 01001000 )"
}
