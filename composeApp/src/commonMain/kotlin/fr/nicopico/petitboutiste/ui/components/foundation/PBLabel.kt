package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.utils.compose.Slot
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography

enum class PBLabelOrientation {
    Vertical,
    Horizontal,
}

@Composable
fun PBLabel(
    label: String,
    modifier: Modifier = Modifier,
    orientation: PBLabelOrientation = PBLabelOrientation.Vertical,
    content: Slot,
) {
    if (orientation == PBLabelOrientation.Vertical) {
        Column(modifier) {
            Text(label, style = JewelTheme.typography.medium)
            Spacer(Modifier.height(4.dp))
            content()
        }
    } else {
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = JewelTheme.typography.medium)
            Spacer(Modifier.width(4.dp))
            content()
        }
    }
}
