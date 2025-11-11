package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.getCharset
import fr.nicopico.petitboutiste.models.representation.arguments.getEndianness
import java.math.BigInteger

fun DataRenderer.decodeBinary(byteArray: ByteArray): String {
    require(this == DataRenderer.Binary)
    return byteArray.joinToString(" ") { byte ->
        val binaryString = byte.toUByte().toString(2).padStart(8, '0')
        "${binaryString.take(4)} ${binaryString.substring(4)}"
    }
}

private val customHexFormat = HexFormat {
    upperCase = true
    bytes {
        byteSeparator = " "
    }
    number {
        removeLeadingZeros = false
    }
}

fun DataRenderer.decodeHexadecimal(byteArray: ByteArray): String {
    require(this == DataRenderer.Hexadecimal)
    return byteArray.toHexString(customHexFormat)
}

fun DataRenderer.decodeInteger(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Integer)
    if (getEndianness(argumentValues) == Endianness.LittleEndian) {
        byteArray.reverse()
    }
    return BigInteger(byteArray).toString(10)
}

fun DataRenderer.decodeUnsignedInteger(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.UnsignedInteger)
    if (getEndianness(argumentValues) == Endianness.LittleEndian) {
        byteArray.reverse()
    }
    return BigInteger(1, byteArray).toString(10)
}

fun DataRenderer.decodeText(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Text)
    if (getEndianness(argumentValues) == Endianness.LittleEndian) {
        byteArray.reverse()
    }
    val charset = getCharset(argumentValues)
    return String(byteArray, charset)
}
