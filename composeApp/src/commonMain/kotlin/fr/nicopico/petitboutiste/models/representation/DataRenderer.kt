/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation

import androidx.compose.runtime.Immutable
import fr.nicopico.petitboutiste.models.representation.arguments.ArgKey
import fr.nicopico.petitboutiste.models.representation.arguments.ArgValue
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentType
import fr.nicopico.petitboutiste.models.representation.arguments.ArgumentValues
import fr.nicopico.petitboutiste.models.representation.arguments.CharsetArgument
import fr.nicopico.petitboutiste.models.representation.arguments.EndiannessArgument
import fr.nicopico.petitboutiste.models.representation.arguments.SignednessArgument
import fr.nicopico.petitboutiste.models.representation.decoder.decodeBinary
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
private const val REQUIRE_USER_VALIDATION_DEFAULT: Boolean = false

enum class DataRenderer(
    val arguments: List<Argument> = emptyList(),
    val customLabel: String? = CUSTOM_LABEL_DEFAULT,
    val requireUserValidation: Boolean = REQUIRE_USER_VALIDATION_DEFAULT,
) {
    Off,
    Binary,
    Hexadecimal(EndiannessArgument),
    Integer(EndiannessArgument, SignednessArgument),
    Text(EndiannessArgument, CharsetArgument),
    Protobuf(protobufArguments, requireUserValidation = true),
    UserScript(userScriptArguments, customLabel = "User script", requireUserValidation = true),
    SubTemplate(subTemplateArguments, requireUserValidation = true),
    ;

    constructor(
        vararg arguments: Argument,
        customLabel: String? = CUSTOM_LABEL_DEFAULT,
        requireUserValidation: Boolean = REQUIRE_USER_VALIDATION_DEFAULT,
    ) : this(arguments.toList(), customLabel, requireUserValidation)

    val label: String get() = customLabel ?: name

    @Immutable
    class Argument(
        val key: ArgKey,
        val label: String,
        val type: ArgumentType<*>,
        val defaultValue: ArgValue? = null,
        val hint: String? = null,
    )

    operator fun invoke(byteArray: ByteArray, argumentValues: ArgumentValues): String? {
        return when (this) {
            Off -> ""
            Binary -> decodeBinary(byteArray)
            Hexadecimal -> decodeHexadecimal(byteArray, argumentValues)
            Integer -> decodeInteger(byteArray, argumentValues)
            Text -> decodeText(byteArray, argumentValues)
            Protobuf -> decodeProtobuf(byteArray, argumentValues)
            UserScript -> decodeUserScript(byteArray, argumentValues)
            SubTemplate -> decodeSubTemplate(byteArray, argumentValues)
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
