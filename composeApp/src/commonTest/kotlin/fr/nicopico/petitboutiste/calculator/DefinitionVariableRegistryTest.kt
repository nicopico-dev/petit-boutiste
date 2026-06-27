package fr.nicopico.petitboutiste.calculator

import fr.nicopico.petitboutiste.calculator.models.Variable
import fr.nicopico.petitboutiste.calculator.models.Variable.Payload
import fr.nicopico.petitboutiste.calculator.models.Variable.Property
import fr.nicopico.petitboutiste.calculator.models.VariableDependencies
import fr.nicopico.petitboutiste.models.data.HexString
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Endianness
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.arguments.EndiannessArgument
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DefinitionVariableRegistryTest {

    private operator fun List<VariableDependencies>.get(
        variable: Variable
    ): VariableDependencies = first {
        it.variable == variable
    }

    private operator fun Iterable<VariableDependencies>.contains(
        variable: Variable
    ): Boolean = any { it.variable == variable }

    @Test
    fun `should build a dependency graph`() {
        // GIVEN
        val definitions = listOf(
            ByteGroupDefinition(
                name = "A",
                startFormula = "0",
                endFormula = "4",
            ),
            ByteGroupDefinition(
                name = "B",
                startFormula = "[[A.end]] + 1",
                endFormula = "[[B.start]] + 1",
            ),
            ByteGroupDefinition(
                name = "C",
                startFormula = "[[B.end]] + 1",
                endFormula = "[[B.value]]",
            ),
            ByteGroupDefinition(
                name = "D",
                startFormula = "[[D.end]] - 2",
                endFormula = "[[LAST]]",
            ),
        )
        val registry = DefinitionVariableRegistry(
            definitions = definitions,
        )

        // WHEN
        val graph = registry.dependencyGraph

        // THEN
        assertTrue { graph.size == 5 }

        assertTrue {
            val dependencies = graph[
                Variable(Payload("B"), Property.VALUE)
            ].dependencies

            dependencies.size == 2
                && Variable(Payload("A"), Property.END) in dependencies
                && Variable(Payload("B"), Property.START) in dependencies
        }

        assertTrue {
            val dependencies = graph[
                Variable(Payload("D"), Property.END)
            ].dependencies

            dependencies.size == 1 && Variable.LAST in dependencies
        }
    }

    @Test
    fun `should compute variables values from the data`() = runTest {
        // GIVEN
        val definitions = listOf(
            ByteGroupDefinition(
                name = "A",
                startFormula = "0",
                endFormula = "2",
            ),
            ByteGroupDefinition(
                name = "B",
                startFormula = "[[A.end]] + 1",
                endFormula = "[[B.start]] + 1",
                representation = Representation(
                    dataRenderer = DataRenderer.Integer,
                    argumentValues = mapOf(
                        EndiannessArgument.key to Endianness.LittleEndian.name
                    )
                )
            ),
            ByteGroupDefinition(
                name = "C",
                startFormula = "[[B.end]] + 1",
                endFormula = "[[B.value]]",
            ),
            ByteGroupDefinition(
                name = "D",
                startFormula = "[[D.end]] - 2",
                endFormula = "[[LAST]]",
            ),
        )
        val data = HexString(
            "4C65200500617420736175746120737572206C65206775E97269646F6E"
        )

        val registry = DefinitionVariableRegistry(
            definitions = definitions,
        )

        // WHEN
        val variables = registry.computeVariableValues(data)

        // THEN
        assertEquals(
            mapOf(
                "[[LAST]]" to 28,
                "[[A.end]]" to 2,
                "[[B.start]]" to 3,
                "[[B.end]]" to 4,
                "[[B.value]]" to 5,
                "[[D.end]]" to 28,
            ),
            variables,
        )
    }

    @Test
    fun `should detect dependency cycles and throw an exception`() {
        // GIVEN
        val definitions = listOf(
            ByteGroupDefinition(
                name = "A",
                startFormula = "0",
                endFormula = "[[B.start]] - 1",
            ),
            ByteGroupDefinition(
                name = "B",
                startFormula = "[[A.end]] + 1",
                endFormula = "[[B.start]] + 1",
            ),
        )
        val registry = DefinitionVariableRegistry(
            definitions = definitions,
        )

        // WHEN - THEN
        val exception = assertFailsWith<IllegalArgumentException> {
            registry.dependencyGraph
        }
        assertEquals(
            "Circular dependency detected: 'B.start' -> 'A.end' -> 'B.start'",
            exception.message,
        )
    }
}
