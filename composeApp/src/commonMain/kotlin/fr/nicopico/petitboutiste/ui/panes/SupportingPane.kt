package fr.nicopico.petitboutiste.ui.panes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.DataString
import fr.nicopico.petitboutiste.models.Representation
import fr.nicopico.petitboutiste.models.extensions.name
import fr.nicopico.petitboutiste.models.renderer.DataRenderer
import fr.nicopico.petitboutiste.ui.components.ByteItemRender
import fr.nicopico.petitboutiste.ui.components.definition.ByteGroupDefinitions
import fr.nicopico.petitboutiste.ui.components.foundation.CollapsableStateless
import fr.nicopico.petitboutiste.ui.components.template.TemplateManagement

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.SupportingPane(
    inputData: DataString,
    definitions: List<ByteGroupDefinition>,
    onDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
    onDefinitionSelected: (ByteGroupDefinition?) -> Unit,
    modifier: Modifier = Modifier.Companion,
    selectedByteItem: ByteItem? = null,
    byteItems: List<ByteItem> = emptyList(),
) {
    var collapsedContent: Boolean by remember {
        mutableStateOf(false)
    }
    // ByteItem.Single does not have a definition, we keep a representation here
    // (note that it is possible to create a Group with a single byte)
    var singleByteRepresentation by remember {
        mutableStateOf(Representation(DataRenderer.Off))
    }

    AnimatedPane(modifier) {
        Column {
            TemplateManagement(
                definitions = definitions,
                onTemplateLoaded = onDefinitionsChanged
            )

            HorizontalDivider(Modifier.padding(vertical = 4.dp))

            ByteGroupDefinitions(
                definitions = definitions,
                onDefinitionsChanged = onDefinitionsChanged,
                selectedDefinition = (selectedByteItem as? ByteItem.Group)?.definition,
                onDefinitionSelected = onDefinitionSelected,
                byteItems = byteItems,
                modifier = Modifier.weight(1f)
            )

            // If a byte item is selected, show its content
            // Otherwise, show the representation of the whole payload
            val byteItemToDisplay = selectedByteItem
                ?: if (inputData.isNotEmpty()) {
                    // Create a group representing the entire payload
                    ByteItem.Group(
                        index = 0,
                        bytes = inputData.hexString,
                        name = "Whole Payload"
                    )
                } else null

            if (byteItemToDisplay != null) {
                HorizontalDivider(
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 4.dp),
                )

                CollapsableStateless(
                    title = (byteItemToDisplay.name ?: "[UNNAMED]") + " Content",
                    collapsed = collapsedContent,
                    onToggleCollapsed = { collapsedContent = it },
                ) {
                    ByteItemRender(
                        byteItem = byteItemToDisplay,
                        representation = if (selectedByteItem is ByteItem.Group) {
                            selectedByteItem.definition.representation
                        } else singleByteRepresentation,
                        onRepresentationChanged = { representation ->
                            if (selectedByteItem is ByteItem.Group) {
                                val currentDefinition = selectedByteItem.definition
                                val updatedDefinition = currentDefinition
                                    .copy(representation = representation)

                                onDefinitionsChanged(
                                    definitions.map {
                                        if (it == currentDefinition) updatedDefinition else it
                                    }
                                )
                            } else {
                                singleByteRepresentation = representation
                            }
                        },
                    )
                }
            }
        }
    }
}
