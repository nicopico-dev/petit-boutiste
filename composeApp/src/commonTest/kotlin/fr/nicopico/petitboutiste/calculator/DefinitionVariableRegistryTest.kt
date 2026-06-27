package fr.nicopico.petitboutiste.calculator

import fr.nicopico.petitboutiste.calculator.models.Variable
import fr.nicopico.petitboutiste.calculator.models.Variable.Payload
import fr.nicopico.petitboutiste.calculator.models.Variable.Property
import fr.nicopico.petitboutiste.calculator.models.VariableDependencies
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertTrue

class DefinitionVariableRegistryTest {

    private lateinit var registry: DefinitionVariableRegistry

    @Before
    fun setUp() {
        registry = DefinitionVariableRegistry()
    }

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

        // WHEN
        val graph = registry.buildDependencyGraph(definitions)

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
}
