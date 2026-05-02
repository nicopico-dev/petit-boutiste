/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.state.SnackbarState
import fr.nicopico.petitboutiste.ui.components.foundation.modifier.clickableWithIndication
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop
import org.jetbrains.jewel.ui.component.Text

@Composable
fun PBSnackbar(
    state: SnackbarState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarColors = AppTheme.current.colors.snackbarColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(snackbarColors.backgroundColor)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { /* prevent click-through */ },
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.message,
                color = snackbarColors.contentColor,
            )

            if (state.actionLabel != null && state.onAction != null) {
                Text(
                    text = state.actionLabel.uppercase(),
                    color = snackbarColors.actionTextColor,
                    modifier = Modifier
                        .background(snackbarColors.actionBackgroundColor)
                        .clip(RoundedCornerShape(2.dp))
                        .clickableWithIndication {
                            state.onAction.invoke()
                            onDismiss()
                        }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PBSnackbarPreview() {
    WrapForPreviewDesktop(SnackbarStatePreviewParameter) { snackbarState ->
        PBSnackbar(snackbarState, onDismiss = {})
    }
}

private object SnackbarStatePreviewParameter : PreviewParameterProvider<SnackbarState> {
    override val values: Sequence<SnackbarState> = sequenceOf(
        SnackbarState("Test message", null, null),
        SnackbarState("Test message with action", "Action", {}),
    )
}
