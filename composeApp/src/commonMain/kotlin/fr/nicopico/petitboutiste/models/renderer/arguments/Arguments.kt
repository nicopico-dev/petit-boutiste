package fr.nicopico.petitboutiste.models.renderer.arguments

typealias ArgKey = String
typealias ArgValue = String
typealias ArgumentValues = Map<ArgKey, ArgValue>

fun emptyArgumentValues(): ArgumentValues = emptyMap()
