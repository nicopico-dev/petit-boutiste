package fr.nicopico.petitboutiste.ui.components.input

import fr.nicopico.petitboutiste.models.input.DataString

interface DataInputAdapter<T: DataString> {
    /**
     * Placeholder to show when empty
     */
    val placeholder: String

    /**
     * Parse [input] to the target value
     */
    fun parse(input: String): T?

    /**
     * Get a normalized String for [value]
     */
    fun getNormalizedString(value: T): String
}
