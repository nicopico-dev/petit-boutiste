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
        val name: String? = null,
    ) : ByteItem() {
        init {
            require(bytes.isNotEmpty()) {
                "bytes must not be empty"
            }
        }

        override fun toString(): String {
            return bytes.joinToString(separator = "")
        }
    }
}
