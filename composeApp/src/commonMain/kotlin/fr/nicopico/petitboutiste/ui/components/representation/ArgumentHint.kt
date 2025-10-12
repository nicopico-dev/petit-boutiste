package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArgumentHint(
    hint: String,
    modifier: Modifier = Modifier,
) {
    Tooltip(
        tooltip = {
            Text(hint)
        },
        content = {
            Icon(
                key = AllIconsKeys.General.QuestionDialog,
                contentDescription = hint,
                modifier = modifier.size(12.dp),
            )
        }
    )
}
