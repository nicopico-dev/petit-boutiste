package fr.nicopico.petitboutiste.models

private val BYTE_VALUE_REGEX = Regex("[a-fA-F0-9]{2}")

sealed class ByteItem {
    data class Single(
        val value: String,
    ) : ByteItem() {

        init {
            require(value.matches(BYTE_VALUE_REGEX)) {
                "value must be a single byte in hex format"
            }
        }

        override fun toString(): String {
            return value
        }
    }

    data class Group(
        val bytes: List<ByteItem>,
        val definition: ByteGroupDefinition,
    ) : ByteItem() {

        constructor(
            bytes: List<ByteItem>,
            name: String? = null,
        ) : this(bytes, ByteGroupDefinition(0..bytes.lastIndex, name))

        init {
            require(bytes.isNotEmpty()) {
                "bytes must not be empty"
            }
            require(bytes.size == ((definition.indexes.last + 1) - definition.indexes.first)) {
                "bytes length must match the indexes of the definition"
            }
        }

        val name: String? = definition.name

        override fun toString(): String {
            return bytes.joinToString(separator = "")
        }
    }
}
