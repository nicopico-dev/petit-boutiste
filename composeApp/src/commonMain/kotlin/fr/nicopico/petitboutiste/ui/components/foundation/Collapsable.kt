package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A composable that provides collapsable behavior.
 *
 * @param title The title to display in the header
 * @param modifier The modifier to apply to the component
 * @param initialCollapsed Whether the component should be initially collapsed
 * @param content The content to display when the component is expanded
 */
@Composable
fun Collapsable(
    title: String,
    modifier: Modifier = Modifier,
    initialCollapsed: Boolean = false,
    content: @Composable () -> Unit
) {
    var collapsed by remember { mutableStateOf(initialCollapsed) }

    CollapsableStateless(
        title = title,
        collapsed = collapsed,
        onToggleCollapsed = { collapsed = it },
        modifier = modifier,
        content = content
    )
}

/**
 * A stateless version of the Collapsable composable that allows external control of the collapsed state.
 *
 * @param title The title to display in the header
 * @param collapsed Whether the component is currently collapsed
 * @param onToggleCollapsed Callback when the collapsed state should change
 * @param modifier The modifier to apply to the component
 * @param content The content to display when the component is expanded
 */
@Composable
fun CollapsableStateless(
    title: String,
    collapsed: Boolean,
    onToggleCollapsed: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Row(
            Modifier.clickable { onToggleCollapsed(!collapsed) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                if (collapsed) Icons.Default.UnfoldMore else Icons.Default.UnfoldLess,
                "Toggle",
                modifier = Modifier.size(18.dp)
            )
        }

        if (!collapsed) {
            content()
        }
    }
}
