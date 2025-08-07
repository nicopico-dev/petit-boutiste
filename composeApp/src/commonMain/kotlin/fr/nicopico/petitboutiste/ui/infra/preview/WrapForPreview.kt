package fr.nicopico.petitboutiste.ui.infra.preview

import fr.nicopico.petitboutiste.ui.PetitBoutisteTheme
import androidx.compose.runtime.Composable

@Composable
fun WrapForPreview(
    content: @Composable () -> Unit
) {
    PetitBoutisteTheme {
        content()
    }
}
