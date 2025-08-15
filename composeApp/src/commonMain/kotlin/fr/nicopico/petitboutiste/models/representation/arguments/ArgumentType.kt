package fr.nicopico.petitboutiste.models.representation.arguments

import java.io.File
import kotlin.reflect.KClass

sealed class ArgumentType<T : Any>(
    private val type: KClass<T>
) {
    abstract fun convertFrom(argValue: ArgValue): T
    abstract fun convertTo(value: T): ArgValue

    fun matches(expectedType: KClass<*>): Boolean = type.java.isAssignableFrom(expectedType.java)

    data object FileType : ArgumentType<File>(File::class) {
        override fun convertFrom(argValue: String): File = File(argValue).absoluteFile
        override fun convertTo(value: File): ArgValue = value.absolutePath
    }

    data object StringType : ArgumentType<String>(String::class) {
        override fun convertFrom(argValue: String): String = argValue
        override fun convertTo(value: String): String = value
    }

    data class ChoiceType<T: Any>(
        private val type: KClass<T>,
        val choices: List<T>,
        private val argValueConverter: (ArgValue) -> T,
        private val choiceConverter: (T) -> ArgValue,
    ) : ArgumentType<T>(type) {

        override fun convertFrom(argValue: String): T = argValueConverter(argValue)
        override fun convertTo(value: T): ArgValue = choiceConverter(value)

        @Suppress("UNCHECKED_CAST")
        fun convertChoice(choice: Any): ArgValue = convertTo(choice as T)
    }
}
