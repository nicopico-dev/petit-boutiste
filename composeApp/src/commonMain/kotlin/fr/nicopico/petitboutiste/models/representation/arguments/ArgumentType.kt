/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.arguments

import fr.nicopico.petitboutiste.utils.file.asString
import fr.nicopico.petitboutiste.utils.nowInMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.io.files.Path
import kotlin.reflect.KClass

sealed class ArgumentType<T : Any>(
    open val type: KClass<T>
) {
    abstract fun convertFrom(argValue: ArgValue): T
    abstract fun convertTo(value: T): ArgValue

    data object FileType : ArgumentType<Path>(Path::class) {
        private const val SEPARATOR = ";;"

        override fun convertFrom(argValue: ArgValue): Path {
            // Ignore the timestamp
            val filePath = argValue.substringBefore(SEPARATOR)
            return Path(filePath)
        }

        override fun convertTo(value: Path): ArgValue {
            // Append a timestamp to the file path to allow reloading the same file
            return value.asString() + SEPARATOR + nowInMillis()
        }
    }

    data object StringType : ArgumentType<String>(String::class) {
        override fun convertFrom(argValue: ArgValue): String = argValue
        override fun convertTo(value: String): ArgValue = value
    }

    data class NumericType<T: Number>(
        override val type: KClass<T>,
        private val argValueConverter: (ArgValue) -> T,
        private val numberConverter: (T) -> ArgValue,
    ) : ArgumentType<T>(type) {

        val isDecimal: Boolean = when(type) {
            Double::class, Float::class -> true
            else -> false
        }

        override fun convertFrom(argValue: ArgValue): T = argValueConverter(argValue)
        override fun convertTo(value: T): ArgValue = numberConverter(value)
    }

    data class ChoiceType<T: Any>(
        override val type: KClass<T>,
        val getChoices: (Flow<ArgumentValues>) -> Flow<List<T>>,
        private val argValueConverter: (ArgValue) -> T,
        private val choiceConverter: (T) -> ArgValue,
    ) : ArgumentType<T>(type) {

        constructor(
            type: KClass<T>,
            choices: List<T>,
            argValueConverter: (ArgValue) -> T,
            choiceConverter: (T) -> ArgValue,
        ) : this(type, { flowOf(choices) }, argValueConverter, choiceConverter)

        override fun convertFrom(argValue: ArgValue): T = argValueConverter(argValue)
        override fun convertTo(value: T): ArgValue = choiceConverter(value)

        @Suppress("UNCHECKED_CAST")
        fun convertChoice(choice: Any): ArgValue = convertTo(choice as T)
    }
}

expect fun ArgumentType<*>.matches(expectedType: KClass<*>): Boolean
