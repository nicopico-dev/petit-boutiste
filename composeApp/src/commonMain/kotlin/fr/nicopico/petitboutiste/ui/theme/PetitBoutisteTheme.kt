package fr.nicopico.petitboutiste.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PetitBoutisteColorScheme = lightColorScheme(
    primary = Color(0xFF1F3D91),
    primaryContainer = Color(0xFFDEE4F8),
    surfaceVariant = Color(0xFFE2F4FB),
    onSurfaceVariant = Color(0xFF1C1B1F)
)

@Composable
fun PetitBoutisteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PetitBoutisteColorScheme,
        content = content
    )
}
