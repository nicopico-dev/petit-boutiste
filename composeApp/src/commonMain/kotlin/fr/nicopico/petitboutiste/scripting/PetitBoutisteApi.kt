package fr.nicopico.petitboutiste.scripting

interface PetitBoutisteApi {
    fun debug(message: String)
    fun error(message: String)
    fun getPayload(): ByteArray
}
