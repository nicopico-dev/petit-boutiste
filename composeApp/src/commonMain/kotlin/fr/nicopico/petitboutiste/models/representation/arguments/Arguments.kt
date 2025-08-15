package fr.nicopico.petitboutiste.models.representation.arguments

typealias ArgKey = String
typealias ArgValue = String
typealias ArgumentValues = Map<ArgKey, ArgValue>

fun emptyArgumentValues(): ArgumentValues = emptyMap()
