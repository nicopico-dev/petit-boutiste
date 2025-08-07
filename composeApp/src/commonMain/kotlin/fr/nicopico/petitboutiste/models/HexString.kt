package fr.nicopico.petitboutiste.models

class HexString(
    rawHexString: String
) : DataString {

    override val hexString: String = rawHexString.normalizeHexString()

    init {
        require(hexString.length % 2 == 0) { "HexString must have an even length" }
    }

    override fun isNotEmpty() = hexString.isNotEmpty()

    companion object {
        fun parse(input: String): HexString? {
            return try {
                HexString(input)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}

private fun String.normalizeHexString(): String = this
    .filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }
    .uppercase()
