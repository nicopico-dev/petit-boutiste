/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness
import fr.nicopico.petitboutiste.models.representation.Signedness
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.ResolutionArgument
import fr.nicopico.petitboutiste.models.representation.arguments.getCharset
import fr.nicopico.petitboutiste.models.representation.arguments.getEndianness
import fr.nicopico.petitboutiste.models.representation.arguments.getSignedness
import fr.nicopico.petitboutiste.utils.toString

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

        // Unicode Thin Space (https://www.compart.com/en/unicode/U+2009)
        groupSeparator = "\u2009"
        bytesPerGroup = 2

        bytesPerLine = 16
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
    return _decodeInteger(byteArray, argumentValues)
}

@Suppress("FunctionName")
private fun DataRenderer._decodeInteger(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    applyEndiannessInPlace(byteArray, argumentValues)

    val isSigned = getSignedness(argumentValues) == Signedness.Signed
    return if (isSigned && byteArray.isNegativeTwoComplement()) {
        "-${byteArray.twosComplementMagnitude().toUnsignedDecimalString()}"
    } else {
        byteArray.toUnsignedDecimalString()
    }
}

fun DataRenderer.decodeDouble(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Double)
    val decodedInt = _decodeInteger(byteArray, argumentValues)
    val intValue = decodedInt.toIntOrNull()
    val resolution = requireNotNull(getArgumentValue<Double>(ResolutionArgument.key, argumentValues))

    return if (intValue != null) {
        (intValue * resolution).toString()
    } else decodedInt
}

fun DataRenderer.decodeText(byteArray: ByteArray, argumentValues: ArgumentValues): String {
    require(this == DataRenderer.Text)
    applyEndiannessInPlace(byteArray, argumentValues)
    val charset = getCharset(argumentValues)
    return byteArray.toString(charset)
}

private fun DataRenderer.applyEndiannessInPlace(
    byteArray: ByteArray,
    argumentValues: ArgumentValues,
) {
    if (getEndianness(argumentValues) == Endianness.LittleEndian) {
        byteArray.reverse()
    }
}

//region Integer decoding
private fun ByteArray.isNegativeTwoComplement(): Boolean {
    return isNotEmpty() && first().toInt() < 0
}

private fun ByteArray.twosComplementMagnitude(): ByteArray {
    val magnitude = ByteArray(size)
    var carry = 1

    for (index in indices.reversed()) {
        val inverted = this[index].toInt().inv() and 0xFF
        val value = inverted + carry
        magnitude[index] = value.toByte()
        carry = value ushr 8
    }

    return magnitude
}

private fun ByteArray.toUnsignedDecimalString(): String {
    if (isEmpty()) return "0"

    val decimalDigits = mutableListOf(0)

    for (byte in this) {
        var carry = byte.toInt() and 0xFF

        for (index in decimalDigits.indices) {
            val value = decimalDigits[index] * 256 + carry
            decimalDigits[index] = value % 10
            carry = value / 10
        }

        while (carry > 0) {
            decimalDigits += carry % 10
            carry /= 10
        }
    }

    return decimalDigits
        .asReversed()
        .joinToString("")
        .trimStart('0')
        .ifEmpty { "0" }
}
//endregion
