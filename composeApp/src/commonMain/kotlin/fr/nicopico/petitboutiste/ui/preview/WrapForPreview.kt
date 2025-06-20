package fr.nicopico.petitboutiste.ui.preview

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WrapForPreview(
    content: @Composable () -> Unit
) {
    MaterialTheme{
        content()
    }
}
