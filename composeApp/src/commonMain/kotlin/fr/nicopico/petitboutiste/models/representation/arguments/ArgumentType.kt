/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.arguments

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import kotlin.reflect.KClass

sealed class ArgumentType<T : Any>(
    private val type: KClass<T>
) {
    abstract fun convertFrom(argValue: ArgValue): T
    abstract fun convertTo(value: T): ArgValue

    fun matches(expectedType: KClass<*>): Boolean {
        return type.javaObjectType.isAssignableFrom(expectedType.javaObjectType)
    }

    // TODO Replace File with KotlinX Path
    data object FileType : ArgumentType<File>(File::class) {
        private const val SEPARATOR = ";;"

        override fun convertFrom(argValue: ArgValue): File {
            // Ignore the timestamp
            val filePath = argValue.substringBefore(SEPARATOR)
            return File(filePath).absoluteFile
        }

        override fun convertTo(value: File): ArgValue {
            // Append a timestamp to the file path to allow reloading the same file
            return value.absolutePath + SEPARATOR + System.currentTimeMillis()
        }
    }

    data object StringType : ArgumentType<String>(String::class) {
        override fun convertFrom(argValue: ArgValue): String = argValue
        override fun convertTo(value: String): ArgValue = value
    }

    data class NumericType<T: Number>(
        private val type: KClass<T>,
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
        private val type: KClass<T>,
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
