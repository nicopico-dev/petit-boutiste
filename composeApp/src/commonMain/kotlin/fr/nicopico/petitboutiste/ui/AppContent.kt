package fr.nicopico.petitboutiste.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.toByteItems
import fr.nicopico.petitboutiste.ui.components.ByteGroupControls
import fr.nicopico.petitboutiste.ui.components.HexDisplay
import fr.nicopico.petitboutiste.ui.components.HexInput
import fr.nicopico.petitboutiste.ui.preview.WrapForPreview

@Composable
fun AppContent(
    data: HexString,
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    onDataChanged: (HexString) -> Unit,
    onGroupDefinitionsChanged: (List<ByteGroupDefinition>) -> Unit,
) {
    var selectedGroupIndex: Int? by remember(groupDefinitions) {
        mutableStateOf(null)
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val byteItems = remember(data, groupDefinitions) {
                data.toByteItems(groupDefinitions)
            }

            Text(
                text = "Hex Input",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HexInput(
                value = data,
                onValueChange = { onDataChanged(it) },
            )

            Spacer(Modifier.height(16.dp))

            HexDisplay(byteItems, modifier = Modifier.weight(1f))

            ByteGroupControls(
                groupDefinition = selectedGroupIndex?.let { groupDefinitions[it] },
                modifier = Modifier.align(Alignment.End),
                onDefinitionChanged = { savedDefinition ->
                    onGroupDefinitionsChanged(
                        groupDefinitions.updateGroupDefinitions(selectedGroupIndex, savedDefinition)
                    )
                }
            )
        }
    }
}

private fun List<ByteGroupDefinition>.updateGroupDefinitions(
    selectedIndex: Int?,
    definition: ByteGroupDefinition,
): List<ByteGroupDefinition> {
    val update = if (selectedIndex == null) {
        this + definition
    } else {
        this.toMutableList()
            .apply {
                set(selectedIndex, definition)
            }
            .toList()
    }
    return update.sortedBy { it.indexes.start }
}

@Preview
@Composable
private fun AppContentPreview() {
    WrapForPreview {
        AppContent(
            HexString(rawHexString = "33DAADDAAD"),
            onDataChanged = {},
            onGroupDefinitionsChanged = {}
        )
    }
}
