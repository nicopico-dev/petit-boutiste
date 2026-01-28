/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils.compose.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
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
            CompositionLocalProvider(
                LocalLifecycleOwner provides PreviewLifecycleOwner
            ) {
                content()
            }
        }
    }
}

private object PreviewLifecycleOwner : LifecycleOwner {
    override val lifecycle: Lifecycle = LifecycleRegistry(this)
}
