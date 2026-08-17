package fr.nicopico.petitboutiste.models.definition

object ByteGroupDefinitionSorter : Comparator<ByteGroupDefinition> {
    override fun compare(o1: ByteGroupDefinition, o2: ByteGroupDefinition): Int {
        val start1 = o1.startFormula.toIntOrNull()
        val start2 = o2.startFormula.toIntOrNull()

        return when {
            start1 != null && start2 != null -> start1.compareTo(start2)
            start1 != null -> -1 // Constants before formulas
            start2 != null -> 1 // Formulas after constants
            else -> 0 // Keep original order if both are formulas
        }
    }
}
