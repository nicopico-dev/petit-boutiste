package fr.nicopico.petitboutiste.models.representation

sealed class RenderResult {
    data object None: RenderResult()
    data class Success(val data: String) : RenderResult()
    data class Error(val message: String) : RenderResult()
}

fun RenderResult.asString(): String? = when (this) {
    is RenderResult.Error -> null
    is RenderResult.None -> ""
    is RenderResult.Success -> data
}
