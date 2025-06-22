package fr.nicopico.petitboutiste.models

private val BYTE_VALUE_REGEX = Regex("[a-fA-F0-9]{2}")

sealed class ByteItem {

    abstract val firstIndex: Int
    abstract val lastIndex: Int

    data class Single(
        val index: Int,
        val value: String,
    ) : ByteItem() {

        init {
            require(value.matches(BYTE_VALUE_REGEX)) {
                "value must be a single byte in hex format"
            }
        }

        override val firstIndex: Int = index
        override val lastIndex: Int = index

        override fun toString(): String {
            return value
        }
    }

    data class Group(
        val bytes: List<String>,
        val definition: ByteGroupDefinition,
    ) : ByteItem() {

        constructor(
            index: Int,
            bytes: String,
            name: String? = null,
        ) : this(bytes.windowed(2, 2), ByteGroupDefinition(index..(index + bytes.length - 1), name))

        init {
            require(bytes.isNotEmpty()) {
                "bytes must not be empty"
            }
            require(bytes.size == ((definition.indexes.last + 1) - definition.indexes.first)) {
                "bytes length must match the indexes of the definition"
            }
            require(bytes.all { it.length == 2 }) {
                "Each bytes must have a length of 2"
            }
        }

        val name: String? = definition.name

        override val firstIndex: Int = definition.indexes.first
        override val lastIndex: Int = definition.indexes.last

        override fun toString(): String {
            return bytes.joinToString(separator = "")
        }
    }
}
