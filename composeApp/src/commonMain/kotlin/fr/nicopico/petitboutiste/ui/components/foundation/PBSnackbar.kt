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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.state.SnackbarState
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import org.jetbrains.jewel.ui.component.Text

@Composable
fun PBSnackbar(
    state: SnackbarState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF323232)) // Standard snackbar dark background
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { /* prevent click-through */ },
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.message,
                color = Color.White,
            )

            if (state.actionLabel != null && state.onAction != null) {
                Text(
                    text = state.actionLabel.uppercase(),
                    color = AppTheme.current.colors.dangerousActionColor, // Or another highlight color
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .clickable {
                            state.onAction.invoke()
                            onDismiss()
                        }
                        .padding(4.dp)
                )
            }
        }
    }
}
