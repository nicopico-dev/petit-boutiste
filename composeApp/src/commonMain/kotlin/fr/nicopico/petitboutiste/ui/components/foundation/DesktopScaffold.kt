package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.styles
import fr.nicopico.petitboutiste.utils.compose.Slot
import fr.nicopico.petitboutiste.utils.compose.optionalSlot
import org.jetbrains.jewel.ui.component.HorizontalSplitLayout
import org.jetbrains.jewel.ui.component.VerticalSplitLayout
import org.jetbrains.jewel.ui.component.rememberSplitLayoutState

@Composable
fun DesktopScaffold(
    main: Slot,
    side: Slot,
    tools: Slot? = null,
) {
    val dividerStyle = AppTheme.current.styles.dividerStyle

    HorizontalSplitLayout(
        first = tools?.optionalSlot { tools ->
            VerticalSplitLayout(
                first = main,
                second = tools,
                state = rememberSplitLayoutState(0.67f),
                dividerStyle = dividerStyle,
                draggableWidth = 16.dp,
                firstPaneMinWidth = 200.dp,
                secondPaneMinWidth = 100.dp,
            )
        } ?: main,
        second = side,
        state = rememberSplitLayoutState(0.70f),
        dividerStyle = dividerStyle,
        draggableWidth = 16.dp,
        firstPaneMinWidth = 300.dp,
        secondPaneMinWidth = 250.dp,
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, color = dividerStyle.color),
    )
}
