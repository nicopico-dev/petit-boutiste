/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation

import androidx.compose.runtime.Immutable
import fr.nicopico.petitboutiste.models.representation.RenderResult.Simple
import fr.nicopico.petitboutiste.models.representation.arguments.ArgKey
import fr.nicopico.petitboutiste.models.representation.arguments.ArgValue
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.CharsetArgument
import fr.nicopico.petitboutiste.models.representation.arguments.EndiannessArgument
import fr.nicopico.petitboutiste.models.representation.arguments.ResolutionArgument
import fr.nicopico.petitboutiste.models.representation.arguments.SignednessArgument
import fr.nicopico.petitboutiste.models.representation.decoder.decodeBinary
import fr.nicopico.petitboutiste.models.representation.decoder.decodeDouble
import fr.nicopico.petitboutiste.models.representation.decoder.decodeHexadecimal
import fr.nicopico.petitboutiste.models.representation.decoder.decodeInteger
import fr.nicopico.petitboutiste.models.representation.decoder.decodeProtobuf
import fr.nicopico.petitboutiste.models.representation.decoder.decodeSubTemplate
import fr.nicopico.petitboutiste.models.representation.decoder.decodeText
import fr.nicopico.petitboutiste.models.representation.decoder.decodeUserScript
import fr.nicopico.petitboutiste.models.representation.decoder.protobufArguments
import fr.nicopico.petitboutiste.models.representation.decoder.subTemplateArguments
import fr.nicopico.petitboutiste.models.representation.decoder.userScriptArguments

private val CUSTOM_LABEL_DEFAULT: String? = null
private val OFF_RENDER_RESULT = Simple("")

enum class DataRenderer(
    val arguments: List<Argument> = emptyList(),
    val customLabel: String? = CUSTOM_LABEL_DEFAULT,
) {
    Off,
    Binary,
    Hexadecimal(EndiannessArgument),
    Integer(EndiannessArgument, SignednessArgument),
    Double(EndiannessArgument, SignednessArgument, ResolutionArgument),
    Text(EndiannessArgument, CharsetArgument),
    Protobuf(protobufArguments),
    UserScript(userScriptArguments, customLabel = "User script"),
    SubTemplate(subTemplateArguments),
    ;

    constructor(
        vararg arguments: Argument,
        customLabel: String? = CUSTOM_LABEL_DEFAULT,
    ) : this(arguments.toList(), customLabel)

    val label: String get() = customLabel ?: name

    @Immutable
    class Argument(
        val key: ArgKey,
        val label: String,
        val type: ArgumentType<*>,
        val defaultValue: ArgValue? = null,
        val hint: String? = null,
    )

    suspend operator fun invoke(byteArray: ByteArray, argumentValues: ArgumentValues): RenderResult {
        return when (this) {
            Off -> OFF_RENDER_RESULT
            Binary -> decodeBinary(byteArray).asSimple()
            Hexadecimal -> decodeHexadecimal(byteArray, argumentValues).asSimple()
            Integer -> decodeInteger(byteArray, argumentValues).asSimple()
            Double -> decodeDouble(byteArray, argumentValues).asSimple()
            Text -> decodeText(byteArray, argumentValues).asSimple()
            Protobuf -> decodeProtobuf(byteArray, argumentValues).asStructured()
            UserScript -> decodeUserScript(byteArray, argumentValues).asDynamic()
            SubTemplate -> decodeSubTemplate(byteArray, argumentValues).asStructured()
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
