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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.toByteItems
import fr.nicopico.petitboutiste.ui.components.HexDisplay
import fr.nicopico.petitboutiste.ui.components.HexInput
import fr.nicopico.petitboutiste.ui.preview.WrapForPreview

@Composable
fun AppContent(
    data: HexString,
    onDataChanged: (HexString) -> Unit,
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val byteItems = remember(data) { data.toByteItems() }

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

            HexDisplay(byteItems)
        }
    }
}

@Preview
@Composable
private fun AppContentPreview() {
    WrapForPreview {
        AppContent(HexString("33DAADDAAD"), {})
    }
}
