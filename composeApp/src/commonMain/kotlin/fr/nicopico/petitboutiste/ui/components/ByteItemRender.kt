package fr.nicopico.petitboutiste.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import fr.nicopico.petitboutiste.models.Representation
import fr.nicopico.petitboutiste.models.render
import fr.nicopico.petitboutiste.models.renderer.DataRenderer
import fr.nicopico.petitboutiste.ui.components.foundation.Dropdown

@Composable
fun ByteItemRender(
    byteItem: ByteItem,
    representation: Representation,
    onRepresentationChanged: (Representation) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Dropdown(
            items = DataRenderer.entries,
            selection = representation.dataRenderer,
            onItemSelected = {
                onRepresentationChanged(representation.copy(dataRenderer = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (representation.dataRenderer != DataRenderer.Off) {
            var dirty by remember(representation) {
                mutableStateOf(representation.dataRenderer.showSubmitButton)
            }
            val rendererOutput: String? by remember(representation, byteItem) {
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
                    showSubmitButton = representation.dataRenderer.showSubmitButton,
                    onSubmit = {
                        dirty = false
                        onRepresentationChanged(representation.copy(argumentValues = it))
                    },
                    onDirty = {
                        dirty = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!dirty) {
                    rendererOutput?.let { output ->
                        OutlinedTextField(
                            value = output,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .heightIn(max = 300.dp)
                                .fillMaxWidth()
                        )
                    } ?: Text("[ERROR]")
                }
            }
        }
    }
}
