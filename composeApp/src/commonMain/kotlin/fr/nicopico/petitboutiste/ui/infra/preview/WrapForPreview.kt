package fr.nicopico.petitboutiste.ui.infra.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WrapForPreview(
    content: @Composable () -> Unit
) {
    MaterialTheme{
        content()
    }
}
