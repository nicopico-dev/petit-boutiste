package fr.nicopico.petitboutiste.models.definition

object ByteGroupDefinitionSorter : Comparator<ByteGroupDefinition> {
    override fun compare(o1: ByteGroupDefinition, o2: ByteGroupDefinition): Int {
        return o1.indexes.first.compareTo(o2.indexes.first)
    }
}
