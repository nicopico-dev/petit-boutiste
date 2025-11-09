package fr.nicopico.petitboutiste.ui.components.input

import fr.nicopico.petitboutiste.models.input.DataString

interface DataInputAdapter<T: DataString> {
    /** Convert the external value to the raw editable text */
    fun toText(value: T): String

    /** Optional sanitization before validation/parsing (e.g., remove spaces) */
    fun sanitize(input: String): String = input

    /** True when we should attempt to parse and emit (e.g., hex: even length) */
    fun isReady(input: String): Boolean = true

    /** Validate characters/content (e.g., binary -> only 0/1) */
    fun isValid(input: String): Boolean

    /** Parse sanitized text into the target value */
    fun parse(input: String): T?

    /** Optional formatted text for display (e.g., binary -> group every 4 characters) */
    fun formatForDisplay(input: String): String = input

    /** Placeholder to show when empty */
    val placeholder: String
}
