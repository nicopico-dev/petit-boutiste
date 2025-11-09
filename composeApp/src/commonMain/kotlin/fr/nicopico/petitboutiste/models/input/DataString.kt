package fr.nicopico.petitboutiste.models.input

/**
 * Interface representing a data string.
 * This interface is implemented by classes that represent specific types of data strings,
 * such as hexadecimal strings.
 */
sealed interface DataString {
    /**
     * The normalized hexadecimal representation of the data.
     */
    val hexString: String

    /**
     * Checks if the data string is not empty.
     *
     * @return true if the data string is not empty, false otherwise.
     */
    fun isNotEmpty(): Boolean
}
