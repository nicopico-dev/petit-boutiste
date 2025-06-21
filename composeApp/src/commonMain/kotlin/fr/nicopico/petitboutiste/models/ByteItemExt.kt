package fr.nicopico.petitboutiste.models

fun ByteItem.getRepresentation(format: RepresentationFormat): String? {
    return when (format) {
        is RepresentationFormat.Hexadecimal -> this.toString()
        is RepresentationFormat.Decimal -> "[NOT IMPLEMENTED]"
        is RepresentationFormat.Text -> "[NOT IMPLEMENTED]"
    }
}
