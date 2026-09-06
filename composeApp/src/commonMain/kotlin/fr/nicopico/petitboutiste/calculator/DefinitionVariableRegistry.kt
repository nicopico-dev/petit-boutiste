/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.calculator

import fr.nicopico.petitboutiste.calculator.models.Variable
import fr.nicopico.petitboutiste.calculator.models.Variable.Payload
import fr.nicopico.petitboutiste.calculator.models.Variable.Property
import fr.nicopico.petitboutiste.calculator.models.VariableDependencies
import fr.nicopico.petitboutiste.calculator.models.VariableValues
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.definition.expandFormulas
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.asString
import fr.nicopico.petitboutiste.models.representation.render
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting

class DefinitionVariableRegistry(
    val definitions: List<ByteGroupDefinition>,
) {

    private val expandedDefinitions = definitions.map { it.expandFormulas() }

    @get:VisibleForTesting
    val dependencyGraph: List<VariableDependencies> by lazy {
        buildDependencyGraph(expandedDefinitions)
    }

    private val namedDefinitions: Map<Payload, ByteGroupDefinition> by lazy {
        expandedDefinitions
            .filterNot { it.name.isNullOrEmpty() }
            .associateBy { Payload(it.name!!) }
    }

    suspend fun computeVariableValues(
        data: DataString,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): VariableValues = withContext(dispatcher) {
        val variablesToCompute = ArrayDeque<Variable>()
        val knownVariables = mutableSetOf<Variable>()
        val variableValues = mutableMapOf<String, Int>()

        dependencyGraph.forEach { graphRoot ->
            variablesToCompute.pushWithDependencies(graphRoot)
            while (variablesToCompute.isNotEmpty()) {
                val variable = variablesToCompute.removeFirst()
                if (variable !in knownVariables) {
                    variableValues[variable.placeholder] = variable.compute(data, variableValues)
                    knownVariables.add(variable)
                }
            }
        }

        variableValues
    }

    private fun ArrayDeque<Variable>.pushWithDependencies(root: VariableDependencies) {
        if (root.variable !in this) addFirst(root.variable)
        root.dependencies.forEach { dependency ->
            pushWithDependencies(dependency)
        }
    }

    private suspend fun Variable.compute(
        data: DataString,
        variables: Map<String, Int>,
    ): Int {
        return if (property == Property.NONE) {
            when (this) {
                Variable.LAST -> data.getLastByteIndex()
                else -> error("Unexpected variable $this")
            }
        } else {
            val definition = requireNotNull(namedDefinitions[payload])

            when (property) {
                Property.START -> {
                    val expanded = definition.expandFormulas()
                    if (expanded.startFormula != null) {
                        Calculator.computeOrThrow(expanded.startFormula, variables)
                    } else if (expanded.endFormula != null && expanded.lengthFormula != null) {
                        val end = Calculator.computeOrThrow(expanded.endFormula, variables)
                        val length = Calculator.computeOrThrow(expanded.lengthFormula, variables)
                        end - length + 1
                    } else {
                        error("Cannot compute START for definition without startFormula or endFormula+lengthFormula")
                    }
                }

                Property.END -> {
                    val expanded = definition.expandFormulas()
                    if (expanded.endFormula != null) {
                        Calculator.computeOrThrow(expanded.endFormula, variables)
                    } else if (expanded.startFormula != null && expanded.lengthFormula != null) {
                        val start = Calculator.computeOrThrow(expanded.startFormula, variables)
                        val length = Calculator.computeOrThrow(expanded.lengthFormula, variables)
                        start + length - 1
                    } else {
                        error("Cannot compute END for definition without endFormula or startFormula+lengthFormula")
                    }
                }

                Property.VALUE -> {
                    val representation = definition.representation
                    require(
                        representation.dataRenderer == DataRenderer.Integer
                            || representation.dataRenderer == DataRenderer.UserScript
                    ) {
                        "VALUE property on $this is only supported for Integer and UserScript representation"
                    }

                    val startIndex: Int
                    val endIndex: Int
                    val expanded = definition.expandFormulas()
                    
                    if (expanded.startFormula != null && expanded.endFormula != null) {
                        startIndex = Calculator.computeOrThrow(expanded.startFormula, variables)
                        endIndex = Calculator.computeOrThrow(expanded.endFormula, variables)
                    } else if (expanded.startFormula != null && expanded.lengthFormula != null) {
                        startIndex = Calculator.computeOrThrow(expanded.startFormula, variables)
                        val length = Calculator.computeOrThrow(expanded.lengthFormula, variables)
                        endIndex = startIndex + length - 1
                    } else if (expanded.endFormula != null && expanded.lengthFormula != null) {
                        val length = Calculator.computeOrThrow(expanded.lengthFormula, variables)
                        endIndex = Calculator.computeOrThrow(expanded.endFormula, variables)
                        startIndex = endIndex - length + 1
                    } else {
                        error("Cannot compute VALUE for definition without valid formula combination")
                    }

                    val byteItem = data.extractByteItem(definition, startIndex, endIndex)
                    val renderResult = representation.render(byteItem)

                    if (renderResult is RenderResult.Success) {
                        renderResult.asString()?.toIntOrNull()
                            ?: error("Render result for $this could not be converted to an Integer: $renderResult")
                    } else error("Could not compute the value of $this: $renderResult")
                }
            }
        }
    }

    private fun DataString.getLastByteIndex(): Int = (hexStringValue.length / 2) - 1

    private fun DataString.extractByteItem(definition: ByteGroupDefinition, startIndex: Int, endIndex: Int): ByteItem {
        val bytes: List<String> = hexStringValue
            .substring(startIndex * 2, (endIndex + 1) * 2)
            .windowed(2, 2)
        return ByteGroup(
            bytes = bytes,
            definition = definition,
            startIndex = startIndex,
        )
    }

    /**
     * Extract the variables used by [definitions] and build a directed graph of the dependencies:
     * `A -> B` means B must be resolved before A
     */
    private fun buildDependencyGraph(
        definitions: List<ByteGroupDefinition>,
    ): List<VariableDependencies> {
        val variablesToResolve = buildSet {
            definitions.forEach {
                addAll(extractVariables(it.startFormula, it.endFormula, it.lengthFormula))
            }
        }

        val resolvedDependencies = mutableMapOf<Variable, VariableDependencies>()

        val graph: List<VariableDependencies> = variablesToResolve
            .mapNotNull { variable ->
                if (variable !in resolvedDependencies) {
                    variable.solveDependencies(namedDefinitions, resolvedDependencies)
                } else null
            }

        return graph
    }

    companion object {
        /**
         * ```
         * [[PAYLOAD.PROPERTY]]
         * ```
         */
        private val VARIABLE_REGEX = Regex("\\[\\[(\\w+)(?:\\.(\\w+))?]]")

        private val Variable.placeholder: String
            get() = buildString {
                append("[[")
                append(payload.name)
                if (property != Property.NONE) {
                    append('.')
                    append(property.code)
                }
                append("]]")
            }

        private fun Variable.solveDependencies(
            namedDefinitions: Map<Payload, ByteGroupDefinition>,
            resolvedDependencies: MutableMap<Variable, VariableDependencies>,
            pendingVariables: List<Variable> = emptyList(), // to detect cycles
        ): VariableDependencies {
            require(this !in pendingVariables) {
                val cycle = (pendingVariables + this).joinToString(
                    separator = " -> ",
                    transform = { variable ->
                        with(variable) {
                            "'$payload.$property'"
                        }
                    },
                )
                "Circular dependency detected: $cycle"
            }

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
                        variablePayloadDefinition.startFormula,
                        variablePayloadDefinition.endFormula,
                        variablePayloadDefinition.lengthFormula
                    )

                    Property.END -> extractVariables(
                        variablePayloadDefinition.startFormula,
                        variablePayloadDefinition.endFormula,
                        variablePayloadDefinition.lengthFormula
                    )

                    Property.VALUE -> extractVariables(
                        variablePayloadDefinition.startFormula,
                        variablePayloadDefinition.endFormula,
                        variablePayloadDefinition.lengthFormula
                    )
                }
            }

            return VariableDependencies(
                variable = this,
                dependencies = dependencies
                    .map { dependency ->
                        resolvedDependencies.getOrPut(dependency) {
                            dependency.solveDependencies(
                                namedDefinitions = namedDefinitions,
                                resolvedDependencies = resolvedDependencies,
                                pendingVariables = pendingVariables + this,
                            )
                        }
                    },
            )
        }

        private fun extractVariables(vararg formulas: String): Set<Variable> {
            require(formulas.size in 1..3) {
                "expected one to three formulas, but was ${formulas.size}"
            }

            return formulas
                .asSequence()
                .flatMap { VARIABLE_REGEX.findAll(it) }
                .map { match ->
                    Variable(
                        payload = Payload(match.groupValues[1]),
                        property = match.groupValues[2].let { propertyCode ->
                            Property.entries
                                .firstOrNull { it.code == propertyCode }
                                ?: throw IllegalArgumentException("Unknown property code '$propertyCode'")
                        }
                    )
                }
                .toSet()
        }
    }
}
