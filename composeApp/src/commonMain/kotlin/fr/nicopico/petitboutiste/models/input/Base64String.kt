package fr.nicopico.petitboutiste.models.input

class Base64String(
    rawBase64String: String
) : DataString {

    val base64String: String = rawBase64String.normalizeBase64String()

    override val hexString: String
        get() = TODO("Not yet implemented")

    override fun isNotEmpty() = base64String.isNotEmpty()

    companion object {
        fun parse(input: String): Base64String? {
            return try {
                Base64String(input)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        // Convert from HexString to Base64String
        fun fromHexString(hexString: HexString): Base64String {
            TODO("Not yet implemented")
        }
    }
}

// Extension function to normalize base64 string
private fun String.normalizeBase64String(): String = this
