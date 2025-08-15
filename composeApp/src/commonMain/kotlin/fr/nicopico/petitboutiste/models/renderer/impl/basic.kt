package fr.nicopico.petitboutiste.models.renderer.impl

import fr.nicopico.petitboutiste.models.Endianness
import fr.nicopico.petitboutiste.models.renderer.DataRenderer
import fr.nicopico.petitboutiste.models.renderer.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.renderer.arguments.getCharset
import fr.nicopico.petitboutiste.models.renderer.arguments.getEndianness
import java.math.BigInteger

fun DataRenderer.decodeBinary(byteArray: ByteArray): String {
    require(this == DataRenderer.Binary)
    return byteArray.joinToString(" ") { byte ->
        val binaryString = byte.toUByte().toString(2).padStart(8, '0')
        "${binaryString.take(4)} ${binaryString.substring(4)}"
    }
}

fun DataRenderer.decodeHexadecimal(byteArray: ByteArray): String {
    require(this == DataRenderer.Hexadecimal)
    return byteArray.toHexString(HexFormat.UpperCase)
}

fun DataRenderer.decodeInteger(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Integer)
    if (getEndianness(argumentValues) == Endianness.LittleEndian) {
        byteArray.reverse()
    }
    // FIXME `FF` gives `-1`, should be 255
    return BigInteger(byteArray).toString(10)
}

fun DataRenderer.decodeText(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Text)
    if (getEndianness(argumentValues) == Endianness.LittleEndian) {
        byteArray.reverse()
    }
    val charset = getCharset(argumentValues)
    return String(byteArray, charset)
}
