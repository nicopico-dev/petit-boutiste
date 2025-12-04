package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness
import fr.nicopico.petitboutiste.models.representation.Signedness
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.getCharset
import fr.nicopico.petitboutiste.models.representation.arguments.getEndianness
import fr.nicopico.petitboutiste.models.representation.arguments.getSignedness
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
        byteSeparator = ""

        bytesPerGroup = 2
        groupSeparator = "\u2009"

        bytesPerLine = 8
    }
    number {
        removeLeadingZeros = false
    }
}

fun DataRenderer.decodeHexadecimal(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Hexadecimal)
    applyEndiannessInPlace(byteArray, argumentValues)
    return byteArray.toHexString(customHexFormat)
}

fun DataRenderer.decodeInteger(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Integer)
    applyEndiannessInPlace(byteArray, argumentValues)
    return if (getSignedness(argumentValues) == Signedness.Signed) {
        BigInteger(byteArray).toString(10)
    } else {
        BigInteger(1, byteArray).toString(10)
    }
}

fun DataRenderer.decodeText(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Text)
    applyEndiannessInPlace(byteArray, argumentValues)
    val charset = getCharset(argumentValues)
    return String(byteArray, charset)
}

private fun DataRenderer.applyEndiannessInPlace(
    byteArray: ByteArray,
    argumentValues: ArgumentValues,
) {
    if (getEndianness(argumentValues) == Endianness.LittleEndian) {
        byteArray.reverse()
    }
}
