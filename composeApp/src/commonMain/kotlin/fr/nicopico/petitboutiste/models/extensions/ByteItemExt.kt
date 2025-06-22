package fr.nicopico.petitboutiste.models.extensions

import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.ByteItem.Group
import fr.nicopico.petitboutiste.models.ByteItem.Single
import fr.nicopico.petitboutiste.models.Endianness
import fr.nicopico.petitboutiste.models.RepresentationFormat
import java.math.BigInteger

val ByteItem.name: String?
    get() = when (this) {
        is Group -> name
        is Single -> null
    }

val ByteItem.size: Int
    get() = when (this) {
        is Group -> bytes.size
        is Single -> 1
    }

fun ByteItem.toByteArray(): ByteArray {
    val hexString = toString()
    val len = hexString.length
    val data = ByteArray(len / 2)
    for (i in 0 until len step 2) {
        data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
    }
    return data
}

fun ByteItem.getRepresentation(format: RepresentationFormat): String? {
    return when (format) {
        is RepresentationFormat.Hexadecimal -> this.toString()

        is RepresentationFormat.Integer -> {
            val byteArray = this.toByteArray()
            if (format.endianness == Endianness.LittleEndian) {
                byteArray.reverse()
            }
            BigInteger(byteArray).toString(10)
        }

        is RepresentationFormat.Text -> {
            val byteArray = this.toByteArray()
            if (format.endianness == Endianness.LittleEndian) {
                byteArray.reverse()
            }
            String(byteArray, format.charset)
        }
    }
}
