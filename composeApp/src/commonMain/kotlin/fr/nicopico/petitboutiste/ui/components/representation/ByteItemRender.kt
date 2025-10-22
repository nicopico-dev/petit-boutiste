package fr.nicopico.petitboutiste.ui.components.representation

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.log
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.isReady
import fr.nicopico.petitboutiste.models.representation.render
import fr.nicopico.petitboutiste.ui.components.foundation.PBDropdown
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextArea
import org.jetbrains.jewel.ui.typography
import kotlin.math.max

@Composable
fun ByteItemRender(
    byteItem: ByteItem,
    representation: Representation,
    onRepresentationChanged: (Representation) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rendererOutput: RenderResult by remember(byteItem, representation) {
        derivedStateOf {
            if (representation.isReady) {
                representation.render(byteItem)
            } else RenderResult.None
        }
    }

    Row(
        modifier.border(1.dp, JewelThemeUtils.colors.borderColor),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .fillMaxHeight()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                        representation.copy(
                            dataRenderer = dataRenderer,
                            submitCount = 0,
                        )
                    )
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
                onArgumentsChange = { argumentValues, submit ->
                    log("Arguments changed: $argumentValues")
                    onRepresentationChanged(
                        representation.copy(
                            argumentValues = argumentValues,
                            submitCount = if (submit) {
                                representation.incrementedSubmitCount()
                            } else representation.submitCount,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Divider(
            orientation = Orientation.Vertical,
            style = JewelThemeUtils.dividerStyle,
            modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp)
        )

        if (representation.isReady) {
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
                textStyle = JewelTheme.typography.h2TextStyle,
                undecorated = false,
                decorationBoxModifier = Modifier.background(JewelThemeUtils.colors.windowBackgroundColor),
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .fillMaxSize()
                    .padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
            )
        }
    }
}

private fun Representation.incrementedSubmitCount(): Int {
    return if (dataRenderer.requireUserValidation) {
        // Increment `submitCount` each time to force a re-render
        submitCount + 1
    } else {
        // Keep `submitCount` at 1
        max(1, submitCount + 1)
    }
}
