package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.isOff
import fr.nicopico.petitboutiste.models.representation.render
import fr.nicopico.petitboutiste.ui.components.foundation.Dropdown
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import fr.nicopico.petitboutiste.utils.hasDifferentEntriesFrom
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextArea
import org.jetbrains.jewel.ui.typography

@Composable
fun ByteItemRender(
    byteItem: ByteItem,
    representation: Representation,
    onRepresentationChanged: (Representation) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rendererOutput by remember(representation, byteItem) {
        derivedStateOf {
            representation.render(byteItem)
        }
    }

    // Dirty is true when the current arguments are different from the one used for then render
    var dirty by remember(representation.dataRenderer) {
        mutableStateOf(false)
    }
    // Wait for the "Render" button if the renderer requires user validation
    var rendered by remember(representation.dataRenderer) {
        mutableStateOf(!representation.dataRenderer.requireUserValidation)
    }

    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxHeight()
                .border(1.dp, JewelThemeUtils.colors.borderColor)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                "Representation",
                style = JewelTheme.typography.medium,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Dropdown(
                items = DataRenderer.entries,
                selection = representation.dataRenderer,
                onItemSelected = {
                    onRepresentationChanged(representation.copy(dataRenderer = it))
                },
                getItemLabel = DataRenderer::label,
                modifier = Modifier.fillMaxWidth()
            )

            Divider(
                orientation = Orientation.Horizontal,
                style = JewelThemeUtils.dividerStyle,
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        bottom = 8.dp,
                    )
                    .fillMaxWidth(.8f)
                    .align(Alignment.CenterHorizontally)
            )

            RendererForm(
                arguments = representation.dataRenderer.arguments,
                values = representation.argumentValues,
                showSubmitButton = representation.dataRenderer.requireUserValidation,
                onSubmit = { argumentValues ->
                    if (argumentValues hasDifferentEntriesFrom representation.argumentValues) {
                        onRepresentationChanged(representation.copy(argumentValues = argumentValues))
                    }
                    dirty = false
                    rendered = true
                },
                onArgumentsChangeWithoutSubmit = {
                    dirty = true
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!dirty && rendered) {
            TextArea(
                state = remember(rendererOutput) {
                    TextFieldState(
                        initialText = when (val output = rendererOutput) {
                            is RenderResult.Success -> output.data
                            is RenderResult.Error -> "[ERROR] ${output.message}"
                            is RenderResult.None -> ""
                        }
                    )
                },
                readOnly = true,
                outline = when (rendererOutput) {
                    is RenderResult.Success -> Outline.None
                    is RenderResult.Error -> Outline.Error
                    is RenderResult.None -> Outline.Warning
                },
                undecorated = representation.isOff,
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .fillMaxSize(),
            )
        }
    }
}
