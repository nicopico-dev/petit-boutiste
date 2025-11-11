package fr.nicopico.petitboutiste.ui.components.input.base64

import fr.nicopico.petitboutiste.models.input.Base64String
import fr.nicopico.petitboutiste.ui.components.input.DataInputAdapter

object Base64InputAdapter : DataInputAdapter<Base64String> {

    override val placeholder = "Paste BASE64 string here"

    override fun parse(input: String) = Base64String.parse(input)

    override fun getNormalizedString(value: Base64String): String = value.base64String
}
