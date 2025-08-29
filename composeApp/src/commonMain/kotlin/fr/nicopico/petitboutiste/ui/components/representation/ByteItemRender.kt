package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.isOff
import fr.nicopico.petitboutiste.models.representation.render
import fr.nicopico.petitboutiste.ui.components.foundation.Dropdown
import fr.nicopico.petitboutiste.utils.hasDifferentEntriesFrom

@Composable
fun ByteItemRender(
    byteItem: ByteItem,
    representation: Representation,
    onRepresentationChanged: (Representation) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Dropdown(
            items = DataRenderer.entries,
            selection = representation.dataRenderer,
            onItemSelected = {
                onRepresentationChanged(representation.copy(dataRenderer = it))
            },
            getItemLabel = DataRenderer::label,
            modifier = Modifier.fillMaxWidth()
        )

        if (!representation.isOff) {
            var dirty by remember(representation.dataRenderer) {
                mutableStateOf(representation.dataRenderer.requireUserValidation)
            }
            val rendererOutput by remember(representation, byteItem) {
                derivedStateOf { representation.render(byteItem) }
            }

            Column(
                Modifier
                    .border(1.dp, Color.Gray)
                    .padding(2.dp)
                    .border(1.dp, Color.Gray)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                RendererForm(
                    arguments = representation.dataRenderer.arguments,
                    values = representation.argumentValues,
                    showSubmitButton = representation.dataRenderer.requireUserValidation,
                    onSubmit = { argumentValues ->
                        dirty = false
                        if (argumentValues hasDifferentEntriesFrom representation.argumentValues) {
                            onRepresentationChanged(representation.copy(argumentValues = argumentValues))
                        }
                    },
                    onDirty = {
                        dirty = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!dirty) {
                    when (val output = rendererOutput) {
                        is RenderResult.Success -> {
                            OutlinedTextField(
                                value = output.data,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .heightIn(max = 300.dp)
                                    .fillMaxWidth()
                            )
                        }
                        is RenderResult.Error -> {
                            Text("[ERROR] ${output.message}", color = MaterialTheme.colors.error)
                        }
                        is RenderResult.None -> Unit
                    }
                }
            }
        }
    }
}
