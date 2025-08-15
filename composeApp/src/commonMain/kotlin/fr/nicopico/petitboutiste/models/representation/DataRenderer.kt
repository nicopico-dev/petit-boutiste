package fr.nicopico.petitboutiste.models.representation

import fr.nicopico.petitboutiste.models.representation.arguments.ArgKey
import fr.nicopico.petitboutiste.models.representation.arguments.ArgValue
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.CharsetArgument
import fr.nicopico.petitboutiste.models.representation.arguments.EndiannessArgument
import fr.nicopico.petitboutiste.models.representation.decoder.PROTOBUF_ARGUMENTS
import fr.nicopico.petitboutiste.models.representation.decoder.decodeBinary
import fr.nicopico.petitboutiste.models.representation.decoder.decodeHexadecimal
import fr.nicopico.petitboutiste.models.representation.decoder.decodeInteger
import fr.nicopico.petitboutiste.models.representation.decoder.decodeProtobuf
import fr.nicopico.petitboutiste.models.representation.decoder.decodeText
import fr.nicopico.petitboutiste.models.representation.decoder.decodeUnsignedInteger

enum class DataRenderer(
    val arguments: List<Argument> = emptyList(),
    val requireUserValidation: Boolean = false,
    val customLabel: String? = null,
) {
    Off,
    Binary,
    Hexadecimal,
    Integer(EndiannessArgument),
    UnsignedInteger(listOf(EndiannessArgument), customLabel = "Integer (unsigned)"),
    Text(EndiannessArgument, CharsetArgument),
    Protobuf(PROTOBUF_ARGUMENTS, requireUserValidation = true),
    ;

    constructor(vararg arguments: Argument) : this(arguments.toList())

    val label: String get() = customLabel ?: name

    class Argument(
        val key: ArgKey,
        val label: String,
        val type: ArgumentType<*>,
        val defaultValue: ArgValue? = null,
    )

    operator fun invoke(byteArray: ByteArray, argumentValues: ArgumentValues): String? {
        return when (this) {
            Off -> ""
            Binary -> decodeBinary(byteArray)
            Hexadecimal -> decodeHexadecimal(byteArray)
            Integer -> decodeInteger(byteArray, argumentValues)
            UnsignedInteger -> decodeUnsignedInteger(byteArray, argumentValues)
            Text -> decodeText(byteArray, argumentValues)
            Protobuf -> decodeProtobuf(byteArray, argumentValues)
        }
    }

    inline fun <reified T : Any> getArgumentValue(argKey: ArgKey, values: ArgumentValues): T? {
        val definition = arguments.first { it.key == argKey }
        val value = values[argKey] ?: definition.defaultValue

        if (value == null) return null

        @Suppress("UNCHECKED_CAST")
        val type: ArgumentType<T> = definition.type as ArgumentType<T>
        if (type.matches(T::class)) {
            return type.convertFrom(value)
        } else error("Mismatch between argument $definition and expected type ${T::class}")
    }
}
