package fr.nicopico.petitboutiste.calculator.models

data class Variable(
    val payload: Payload,
    val property: Property,
) {
    @JvmInline
    value class Payload(val name: String) {
        override fun toString(): String = name
    }

    enum class Property(val code: String) {
        START("start"),
        END("end"),
        VALUE("value"),
        NONE("")
        ;

        override fun toString(): String = code
    }

    companion object {
        val LAST = Variable(Payload("LAST"), Property.NONE)
    }
}
