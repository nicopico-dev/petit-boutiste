/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.ui.components.foundation.PBDropdown
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.styles
import fr.nicopico.petitboutiste.utils.log
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography

@Composable
fun ByteGroupRepresentationForm(
    representation: Representation,
    onRepresentationChanged: (Representation) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            "Representation",
            style = JewelTheme.typography.h4TextStyle,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        PBDropdown(
            items = DataRenderer.entries,
            selection = representation.dataRenderer,
            onItemSelected = { dataRenderer ->
                onRepresentationChanged(
                    representation.copy(dataRenderer = dataRenderer)
                )
            },
            getItemLabel = DataRenderer::label,
            modifier = Modifier.fillMaxWidth()
        )

        Divider(
            orientation = Orientation.Horizontal,
            style = AppTheme.current.styles.dividerStyle,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 8.dp,
                )
                .fillMaxWidth(.8f)
                .align(Alignment.CenterHorizontally)
        )

        RendererArgumentsForm(
            arguments = representation.dataRenderer.arguments,
            values = representation.argumentValues,
            onArgumentsChange = { argumentValues ->
                log("Arguments changed: $argumentValues")
                onRepresentationChanged(
                    representation.copy(argumentValues = argumentValues)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
