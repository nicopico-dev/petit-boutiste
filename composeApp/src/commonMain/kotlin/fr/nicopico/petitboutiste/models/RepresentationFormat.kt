package fr.nicopico.petitboutiste.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.nio.charset.Charset

@Serializable
sealed class RepresentationFormat {
    @Serializable
    data object Hexadecimal : RepresentationFormat()

    @Serializable
    data class Integer(
        val endianness: Endianness = Endianness.BigEndian,
    ) : RepresentationFormat()

    @Serializable
    data class Text(
        val endianness: Endianness = Endianness.BigEndian,
        @Contextual
        val charset: Charset = Charsets.UTF_8,
    ) : RepresentationFormat()
}
