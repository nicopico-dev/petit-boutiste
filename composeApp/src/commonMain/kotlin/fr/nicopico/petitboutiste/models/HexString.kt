package fr.nicopico.petitboutiste.models

class HexString(
    rawHexString: String
) {

    val hexString: String = rawHexString.normalizeHexString()

    init {
        require(hexString.length % 2 == 0) { "HexString must have an even length" }
    }

    fun isNotEmpty() = hexString.isNotEmpty()
}

private fun String.normalizeHexString(): String = this
    .filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }
    .uppercase()
