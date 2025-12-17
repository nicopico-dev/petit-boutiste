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

    fun matches(expectedType: KClass<*>): Boolean = type.java.isAssignableFrom(expectedType.java)

    data object FileType : ArgumentType<File>(File::class) {
        override fun convertFrom(argValue: ArgValue): File = File(argValue).absoluteFile
        override fun convertTo(value: File): ArgValue = value.absolutePath
    }

    data object StringType : ArgumentType<String>(String::class) {
        override fun convertFrom(argValue: ArgValue): String = argValue
        override fun convertTo(value: String): ArgValue = value
    }

    data class ChoiceType<T: Any>(
        private val type: KClass<T>,
        val getChoices: (ArgumentValues) -> Flow<List<T>>,
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
