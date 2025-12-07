package fr.nicopico.petitboutiste.utils.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.ui.theme.invoke

@Composable
fun WrapForPreview(
    appTheme: PBTheme = PBTheme.System,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit,
) {
    appTheme {
        val backgroundColor = backgroundColor
            ?: appTheme.colors.windowBackgroundColor
        Box(modifier = modifier.background(backgroundColor)) {
            content()
        }
    }
}
