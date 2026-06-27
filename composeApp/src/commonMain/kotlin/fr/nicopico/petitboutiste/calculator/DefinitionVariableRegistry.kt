package fr.nicopico.petitboutiste.calculator

import fr.nicopico.petitboutiste.calculator.models.Variable
import fr.nicopico.petitboutiste.calculator.models.Variable.Payload
import fr.nicopico.petitboutiste.calculator.models.Variable.Property
import fr.nicopico.petitboutiste.calculator.models.VariableDependencies
import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.definition.ByteItem
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.asString
import fr.nicopico.petitboutiste.models.representation.render
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting

class DefinitionVariableRegistry(
    definitions: List<ByteGroupDefinition>,
) {

    @get:VisibleForTesting
    val dependencyGraph: List<VariableDependencies> by lazy {
        buildDependencyGraph(definitions)
    }

    private val namedDefinitions: Map<Payload, ByteGroupDefinition> by lazy {
        definitions
            .filterNot { it.name.isNullOrEmpty() }
            .associateBy { Payload(it.name!!) }
    }

    suspend fun computeVariableValues(
        data: DataString,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): Map<String, Int> = withContext(dispatcher) {
        val knownVariables = mutableMapOf<Variable, Int>()
        val variablesToCompute = ArrayDeque<Variable>()

        dependencyGraph.forEach { graphRoot ->
            variablesToCompute.pushWithDependencies(graphRoot)
            while (variablesToCompute.isNotEmpty()) {
                val variable = variablesToCompute.removeFirst()
                if (variable !in knownVariables) {
                    knownVariables[variable] = variable.compute(data, knownVariables)
                }
            }
        }

        knownVariables.mapKeys { (key, _) -> key.placeholder }
    }

    private fun ArrayDeque<Variable>.pushWithDependencies(root: VariableDependencies) {
        addFirst(root.variable)
        root.dependencies.forEach { dependency ->
            pushWithDependencies(dependency)
        }
    }

    @VisibleForTesting
    suspend fun Variable.compute(
        data: DataString,
        knownVariables: Map<Variable, Int>,
    ): Int {
        return if (property == Property.NONE) {
            when (this) {
                Variable.LAST -> data.getLastByteIndex()
                else -> error("Unexpected variable $this")
            }
        } else {
            val definition = requireNotNull(namedDefinitions[payload])
            val variables = knownVariables.mapKeys { it.key.placeholder }

            when (property) {
                Property.START -> {
                    Calculator.computeOrThrow(definition.startFormula, variables)
                }

                Property.END -> {
                    Calculator.computeOrThrow(definition.endFormula, variables)
                }

                Property.VALUE -> {
                    val representation = definition.representation
                    require(representation.dataRenderer == DataRenderer.Integer) {
                        "VALUE property on $this is only supported for Integer representation"
                    }

                    val startIndex = Calculator.computeOrThrow(definition.startFormula, variables)
                    val endIndex = Calculator.computeOrThrow(definition.endFormula, variables)

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
            .windowed(2, 2)
            .subList(startIndex, endIndex + 1)
        return ByteGroup(bytes, definition)
    }

    /**
     * Extract the variables used by [definitions] and build a directed graph of the dependencies:
     * `A -> B` means B must be resolved before A
     */
    private fun buildDependencyGraph(
        definitions: List<ByteGroupDefinition>,
    ): List<VariableDependencies> {
        val definitionVariables: Map<ByteGroupDefinition, Set<Variable>> = definitions
            .associateWith { definition ->
                extractVariables(definition.startFormula, definition.endFormula)
            }

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
    }
}
