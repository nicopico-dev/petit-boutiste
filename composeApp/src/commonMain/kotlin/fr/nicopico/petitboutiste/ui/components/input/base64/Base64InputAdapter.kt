package fr.nicopico.petitboutiste.ui.components.input.base64

import fr.nicopico.petitboutiste.models.input.Base64String
import fr.nicopico.petitboutiste.ui.components.input.DataInputAdapter

object Base64InputAdapter : DataInputAdapter<Base64String> {
    override fun toText(value: Base64String) = value.base64String

    // parsing will decide; could add stricter validation if desired
    override fun isValid(input: String) = true

    override fun parse(input: String) = Base64String.parse(input)

    override val placeholder = "Paste BASE64 string here"
}
