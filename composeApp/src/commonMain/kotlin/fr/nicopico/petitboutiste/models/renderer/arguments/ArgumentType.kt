package fr.nicopico.petitboutiste.models.renderer.arguments

import java.io.File
import kotlin.reflect.KClass

sealed class ArgumentType<T : Any>(
    private val type: KClass<T>
) {
    abstract fun convert(value: String): T

    fun matches(expectedType: KClass<*>): Boolean = type.java.isAssignableFrom(expectedType.java)

    data object FileType : ArgumentType<File>(File::class) {
        override fun convert(value: String): File = File(value).absoluteFile
    }

    data object StringType : ArgumentType<String>(String::class) {
        override fun convert(value: String): String = value
    }

    data class ChoiceType<T: Any>(
        private val type: KClass<T>,
        val choices: List<T>,
        private val argValueConverter: (ArgValue) -> T,
        private val choiceConverter: (T) -> ArgValue,
    ) : ArgumentType<T>(type) {
        override fun convert(value: String): T = argValueConverter(value)

        @Suppress("UNCHECKED_CAST")
        fun convertChoice(choice: Any): ArgValue = choiceConverter(choice as T)
    }
}
