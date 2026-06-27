package fr.nicopico.petitboutiste.calculator.models

data class VariableDependencies(
    val variable: Variable,
    val dependencies: List<VariableDependencies>,
)
