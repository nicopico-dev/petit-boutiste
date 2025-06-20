package fr.nicopico.petitboutiste

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.models.toByteItems
import fr.nicopico.petitboutiste.ui.components.HexDisplay
import fr.nicopico.petitboutiste.ui.components.HexInput
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //HexInputDisplay()
            var data by rememberSaveable {
                mutableStateOf(HexString(""))
            }
            val byteItems by remember {
                derivedStateOf { data.toByteItems() }
            }

            Text(
                text = "Hex Input",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HexInput(
                value = data,
                onValueChange = { data = it },
            )

            Spacer(Modifier.height(16.dp))

            HexDisplay(byteItems)
        }
    }
}
