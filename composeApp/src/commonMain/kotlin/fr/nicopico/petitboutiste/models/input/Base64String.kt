package fr.nicopico.petitboutiste.models.input

import kotlin.io.encoding.Base64

class Base64String(
    private val byteArray: ByteArray = ByteArray(0),
) : DataString {

    val base64String: String = base64.encode(byteArray)

    override val hexString: String
        get() = byteArray.toHexString()

    override fun isNotEmpty() = base64String.isNotEmpty()

    companion object {
        private val base64: Base64 = Base64.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)

        fun parse(input: String): Base64String? {
            return try {
                val byteArray = base64.decode(input)
                Base64String(byteArray)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        fun fromHexString(hexString: HexString): Base64String {
            val byteArray = hexString.hexString.hexToByteArray()
            return Base64String(byteArray)
        }
    }
}
