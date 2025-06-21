package fr.nicopico.petitboutiste.ui.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.toByteItems
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview
import fr.nicopico.petitboutiste.ui.main.components.HexDisplay
import fr.nicopico.petitboutiste.ui.main.components.HexInput

@Composable
fun MainPane(
    data: HexString,
    modifier: Modifier = Modifier,
    groupDefinitions: List<ByteGroupDefinition> = emptyList(),
    onDataChanged: (HexString) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
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
    }
}

@Preview
@Composable
private fun MainPanePreview() {
    WrapForPreview {
        MainPane(
            HexString(rawHexString = "33DAADDAAD"),
            onDataChanged = {},
        )
    }
}
