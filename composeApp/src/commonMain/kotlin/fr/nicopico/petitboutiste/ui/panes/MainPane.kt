package fr.nicopico.petitboutiste.ui.panes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.DataString
import fr.nicopico.petitboutiste.models.extensions.toByteItems
import fr.nicopico.petitboutiste.models.ui.InputType
import fr.nicopico.petitboutiste.ui.components.BinaryInput
import fr.nicopico.petitboutiste.ui.components.HexDisplay
import fr.nicopico.petitboutiste.ui.components.HexInput
import fr.nicopico.petitboutiste.ui.components.InputTypeToggle

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.MainPane(
    inputData: DataString,
    onInputDataChanged: (DataString) -> Unit,
    modifier: Modifier = Modifier.Companion,
    byteItems: List<ByteItem> = inputData.toByteItems(),
    selectedByteItem: ByteItem? = null,
    onByteItemSelected: (ByteItem?) -> Unit = {},
    inputType: InputType = InputType.HEX,
    onInputTypeChanged: (InputType) -> Unit = {},
) {
    AnimatedPane(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = "Data Input",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Center)
                )

                InputTypeToggle(
                    inputType,
                    onInputTypeChanged,
                    Modifier.align(Alignment.CenterEnd)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Render the appropriate input component based on the selected input type
            val inputModifier = Modifier.heightIn(max = 120.dp)
            when (inputType) {
                InputType.HEX -> HexInput(
                    value = inputData,
                    onValueChange = { onInputDataChanged(it) },
                    modifier = inputModifier
                )

                InputType.BINARY -> BinaryInput(
                    value = inputData,
                    onValueChange = { onInputDataChanged(it) },
                    modifier = inputModifier
                )
            }

            Spacer(Modifier.height(16.dp))

            HexDisplay(
                byteItems,
                selectedByteItem = selectedByteItem,
                modifier = Modifier.weight(1f),
                onByteItemClicked = {
                    onByteItemSelected(if (selectedByteItem != it) it else null)
                },
            )
        }
    }
}
