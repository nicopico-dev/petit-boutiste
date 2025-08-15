package fr.nicopico.petitboutiste.ui.infra.preview

import androidx.compose.runtime.Composable
import fr.nicopico.petitboutiste.ui.theme.PetitBoutisteTheme

@Composable
fun WrapForPreview(
    content: @Composable () -> Unit
) {
    PetitBoutisteTheme {
        content()
    }
}
