package fr.nicopico.petitboutiste.models.definition

// TODO: Index-based sorting is deferred — definitions with variable formulas cannot be statically
//  ordered. Preserving insertion order for now. Revisit in a future session when variable-formula
//  sorting is implemented.
object ByteGroupDefinitionSorter : Comparator<ByteGroupDefinition> {
    override fun compare(o1: ByteGroupDefinition, o2: ByteGroupDefinition): Int {
        return 0
    }
}
