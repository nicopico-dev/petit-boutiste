package fr.nicopico.petitboutiste.models

import java.nio.charset.Charset

sealed class RepresentationFormat {
    data object Hexadecimal : RepresentationFormat()

    data class Integer(
        val endianness: Endianness = Endianness.BigEndian,
    ) : RepresentationFormat()

    data class Text(
        val endianness: Endianness = Endianness.BigEndian,
        val charset: Charset = Charsets.UTF_8,
    ) : RepresentationFormat()
}
