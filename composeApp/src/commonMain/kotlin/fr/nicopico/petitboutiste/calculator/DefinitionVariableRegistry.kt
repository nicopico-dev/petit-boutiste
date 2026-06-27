package fr.nicopico.petitboutiste.calculator

import fr.nicopico.petitboutiste.calculator.models.Variable
import fr.nicopico.petitboutiste.calculator.models.Variable.Payload
import fr.nicopico.petitboutiste.calculator.models.Variable.Property
import fr.nicopico.petitboutiste.calculator.models.VariableDependencies
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import kotlin.collections.emptySet

class DefinitionVariableRegistry {

    /**
     * Extract the variables used by [definitions] and build a directed graph of the dependencies:
     * `A -> B` means B must be resolved before A
     */
    fun buildDependencyGraph(
        definitions: List<ByteGroupDefinition>,
    ): List<VariableDependencies> {
        val definitionVariables: Map<ByteGroupDefinition, Set<Variable>> = definitions
            .associateWith { definition ->
                extractVariables(definition.startFormula, definition.endFormula)
            }

        val namedDefinitions: Map<Payload, ByteGroupDefinition> = definitions
            .filterNot { it.name.isNullOrEmpty() }
            .associateBy { Payload(it.name!!) }

        val variablesToResolve = definitionVariables.values
            .fold(
                initial = emptySet<Variable>(),
                operation = { initial, variables ->
                    initial + variables
                }
            )

        val resolvedDependencies = mutableMapOf<Variable, VariableDependencies>()

        val graph: List<VariableDependencies> = variablesToResolve
            .mapNotNull { variable ->
                if (variable !in resolvedDependencies) {
                    variable.solveDependencies(namedDefinitions, resolvedDependencies)
                } else null
            }

        return graph
    }

    private fun Variable.solveDependencies(
        namedDefinitions: Map<Payload, ByteGroupDefinition>,
        resolvedDependencies: MutableMap<Variable, VariableDependencies>,
    ): VariableDependencies {

        val dependencies = if (property == Property.NONE) {
            // Property.NONE is use for special variables
            when(this) {
                Variable.LAST -> emptySet()
                else -> throw IllegalArgumentException("Unknown dependency $this")
            }
        } else {
            val variablePayloadDefinition = requireNotNull(namedDefinitions[payload]) {
                "Payload $payload is not defined"
            }

            when (property) {
                Property.START -> extractVariables(
                    variablePayloadDefinition.startFormula
                )

                Property.END -> extractVariables(
                    variablePayloadDefinition.endFormula
                )

                Property.VALUE -> extractVariables(
                    variablePayloadDefinition.startFormula,
                    variablePayloadDefinition.endFormula,
                )
            }
        }

        return VariableDependencies(
            variable = this,
            dependencies = dependencies
                .map { dependency ->
                    resolvedDependencies.getOrPut(dependency) {
                        dependency.solveDependencies(namedDefinitions, resolvedDependencies)
                    }
                },
        )
    }

    private fun extractVariables(vararg formulas: String): Set<Variable> {
        require(formulas.size == 1 || formulas.size == 2) {
            "expected one or two formulas, but was ${formulas.size}"
        }

        return formulas
            .asSequence()
            .flatMap { VARIABLE_REGEX.findAll(it) }
            .map { match ->
                if (match.groupValues.size == 2) {
                    val variableName = match.groupValues[1]
                    require(variableName != "LAST") {
                        "Unknown variable $variableName"
                    }
                    Variable.LAST
                } else {
                    Variable(
                        payload = Payload(match.groupValues[1]),
                        property = match.groupValues[2].let { propertyCode ->
                            Property.entries
                                .firstOrNull { it.code == propertyCode }
                                ?: throw IllegalArgumentException("Unknown property code '$propertyCode'")
                        }
                    )
                }
            }
            .toSet()
    }

    companion object {
        /**
         * ```
         * [[PAYLOAD.PROPERTY]]
         * ```
         */
        private val VARIABLE_REGEX = Regex("\\[\\[(\\w+)(?:\\.(\\w+))?]]")
    }

}
