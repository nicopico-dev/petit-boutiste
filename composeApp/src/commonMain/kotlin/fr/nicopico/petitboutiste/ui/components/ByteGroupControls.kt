package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.ui.preview.WrapForPreview

@Composable
fun ByteGroupControls(
    groupDefinition: ByteGroupDefinition?,
    onDefinitionChanged: (ByteGroupDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    var startIndexInput by remember(groupDefinition) {
        mutableStateOf(groupDefinition?.indexes?.start?.toString() ?: "")
    }
    var endIndexInput by remember(groupDefinition) {
        mutableStateOf(groupDefinition?.indexes?.endInclusive?.toString() ?: "")
    }
    var name by remember(groupDefinition) {
        mutableStateOf(groupDefinition?.name ?: "")
    }

    var startIndexError by remember { mutableStateOf<String?>(null) }
    var endIndexError by remember { mutableStateOf<String?>(null) }

    // Validate when inputs change
    LaunchedEffect(startIndexInput, endIndexInput) {
        startIndexError = null
        endIndexError = null

        val startIndex = startIndexInput.toIntOrNull()

        if (startIndexInput.isNotEmpty()) {
            when {
                startIndex == null -> startIndexError = "Must be a number"
                startIndex < 0 -> startIndexError = "Must be a positive number"
            }
        }

        if (endIndexInput.isNotEmpty()) {
            val endIndex = endIndexInput.toIntOrNull()
            when {
                endIndex == null -> endIndexError = "Must be a number"
                endIndex < (startIndex ?: 0) -> endIndexError = "Must be greater than or equal to Start"
            }
        }
    }

    val isValid by remember {
        derivedStateOf {
            startIndexInput != "" && endIndexInput != ""
                && startIndexError == null && endIndexError == null
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = startIndexInput,
            onValueChange = { startIndexInput = it },
            label = { Text("Start") },
            isError = startIndexError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(100.dp),
        )

        OutlinedTextField(
            value = endIndexInput,
            onValueChange = { endIndexInput = it },
            label = { Text("End") },
            isError = endIndexError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(100.dp),
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name?") },
            singleLine = true,
            modifier = Modifier.width(200.dp),
        )

        val buttonColors = ButtonDefaults.buttonColors()
        IconButton(
            content = {
                if (groupDefinition == null) {
                    Icon(Icons.Default.Add, "Add")
                } else {
                    Icon(Icons.Default.Save, "Save")
                }
            },
            colors = IconButtonDefaults.iconButtonColors()
                .copy(
                    containerColor = buttonColors.containerColor,
                    contentColor = buttonColors.contentColor,
                    disabledContainerColor = buttonColors.disabledContainerColor,
                    disabledContentColor = buttonColors.disabledContentColor
                ),
            enabled = isValid,
            onClick = {
                val definition = ByteGroupDefinition(
                    startIndexInput.toInt()..endIndexInput.toInt(),
                    name.ifBlank { null },
                )
                onDefinitionChanged(definition)
            },
            modifier = Modifier.offset(y = 4.dp)
        )
    }
}

@Preview
@Composable
private fun ByteGroupControlsCreatePreview() {
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ByteGroupControls(
                groupDefinition = null,
                onDefinitionChanged = {},
            )
            ByteGroupControls(
                groupDefinition = ByteGroupDefinition(2..5, "Test Group"),
                onDefinitionChanged = {},
            )
        }
    }
}
