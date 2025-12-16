/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.ui.theme.PBTheme
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

private val VERTICAL_SPACE = 8.dp

@Composable
fun WrapForPreviewDesktop(
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACE),
    ) {
        WrapForPreview(PBTheme.Light) {
            content()
        }

        WrapForPreview(PBTheme.Dark) {
            content()
        }
    }
}

@Composable
fun <T> WrapForPreviewDesktop(
    parameterProvider: PreviewParameterProvider<T>,
    content: @Composable (T) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACE),
    ) {
        parameterProvider.values.forEach { item ->
            WrapForPreview(PBTheme.Light) {
                content(item)
            }

            WrapForPreview(PBTheme.Dark) {
                content(item)
            }
        }
    }
}
