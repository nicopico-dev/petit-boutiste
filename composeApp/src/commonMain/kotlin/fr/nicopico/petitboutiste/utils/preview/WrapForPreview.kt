package fr.nicopico.petitboutiste.utils.preview

import androidx.compose.runtime.Composable

@Composable
fun WrapForPreview(
    content: @Composable () -> Unit
) {
    content()
}
