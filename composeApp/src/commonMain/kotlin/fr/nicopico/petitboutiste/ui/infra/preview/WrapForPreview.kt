package fr.nicopico.petitboutiste.ui.infra.preview

import androidx.compose.runtime.Composable

@Composable
fun WrapForPreview(
    content: @Composable () -> Unit
) {
    content()
}
