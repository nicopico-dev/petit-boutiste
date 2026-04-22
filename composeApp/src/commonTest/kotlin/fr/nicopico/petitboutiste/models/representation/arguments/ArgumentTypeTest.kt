package fr.nicopico.petitboutiste.models.representation.arguments

import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ArgumentTypeTest {

    @Test
    fun `matches should handle primitive vs boxed types`() {
        // Given
        val type = Double::class
        val argumentType = ArgumentType.NumericType(type, 0.0, { it.toDouble() }, { it.toString() })

        // When
        val expectedType = Class.forName("java.lang.Double").kotlin
        
        // Then
        assertTrue(argumentType.matches(expectedType), "double.class should match Double.class")
        assertTrue(argumentType.matches(Double::class), "double.class should match double.class")
    }

    @Test
    fun `matches should handle inheritance`() {
        // Given
        val type = Number::class
        val argumentType = ArgumentType.NumericType(type, 0, { it.toInt() }, { it.toString() })

        // Then
        assertTrue(argumentType.matches(Int::class), "Number should match Int")
        assertTrue(argumentType.matches(Double::class), "Number should match Double")
        assertTrue(argumentType.matches(Number::class), "Number should match Number")
    }

    @Test
    fun `matches should return false for incompatible types`() {
        // Given
        val argumentType = ArgumentType.StringType

        // Then
        assertFalse(argumentType.matches(Int::class), "String should not match Int")
        assertFalse(argumentType.matches(Any::class), "String should not match Any")
    }
}
