package fr.nicopico.petitboutiste.models

class HexString(
    rawHexString: String,
    inputFormat: InputFormat = InputFormat.HEXADECIMAL
) {

    val hexString: String = when (inputFormat) {
        InputFormat.HEXADECIMAL -> rawHexString.normalizeHexString()
        InputFormat.BINARY -> rawHexString.normalizeBinaryString().binaryToHex()
    }

    init {
        require(hexString.length % 2 == 0) { "HexString must have an even length" }
    }

    fun isNotEmpty() = hexString.isNotEmpty()

    companion object {
        fun parse(input: String, inputFormat: InputFormat = InputFormat.HEXADECIMAL): HexString? {
            // Validate input format before attempting to create HexString
            val isValid = when (inputFormat) {
                InputFormat.HEXADECIMAL -> input.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }
                InputFormat.BINARY -> input.all { it == '0' || it == '1' }
            }

            if (!isValid) return null

            return try {
                HexString(input, inputFormat)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        fun hexToBinary(hexString: String): String {
            return hexString.map {
                it.digitToInt(16).toString(2).padStart(4, '0')
            }.joinToString("")
        }
    }
}

private fun String.normalizeHexString(): String = this
    .filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }
    .uppercase()

private fun String.normalizeBinaryString(): String = this
    .filter { it == '0' || it == '1' }

private fun String.binaryToHex(): String {
    if (this.isEmpty()) return ""

    // Ensure the binary string length is a multiple of 8 (to get an even number of hex digits)
    val paddedBinary = if (this.length % 8 != 0) {
        this.padStart((this.length / 8 + 1) * 8, '0')
    } else {
        this
    }

    // Convert each 4 bits to a hex character
    val result = StringBuilder()
    for (i in 0 until paddedBinary.length step 4) {
        val fourBits = paddedBinary.substring(i, minOf(i + 4, paddedBinary.length))
        val hexDigit = fourBits.toInt(2).toString(16).uppercase()
        result.append(hexDigit)
    }

    return result.toString()
}
