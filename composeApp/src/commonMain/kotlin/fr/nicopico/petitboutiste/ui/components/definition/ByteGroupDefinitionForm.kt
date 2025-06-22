package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@Composable
fun ByteGroupDefinitionForm(
    definition: ByteGroupDefinition?,
    onDefinitionSaved: (ByteGroupDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    var startIndexInput by remember(definition) {
        mutableStateOf(definition?.indexes?.start?.toString() ?: "")
    }
    var endIndexInput by remember(definition) {
        mutableStateOf(definition?.indexes?.endInclusive?.toString() ?: "")
    }
    var name by remember(definition) {
        mutableStateOf(definition?.name ?: "")
    }

    //region Input validation
    val startIndexError by remember(definition) {
        derivedStateOf {
            if (startIndexInput.isNotEmpty()) {
                val startIndex = startIndexInput.toIntOrNull()
                when {
                    startIndex == null -> "Must be a number"
                    startIndex < 0 -> "Must be a positive number"
                    else -> null
                }
            } else null
        }
    }
    val endIndexError by remember(definition) {
        derivedStateOf {
            if (endIndexInput.isNotEmpty()) {
                val startIndex = startIndexInput.toIntOrNull()
                val endIndex = endIndexInput.toIntOrNull()
                when {
                    endIndex == null -> "Must be a number"
                    endIndex < (startIndex ?: 0) -> "Must be greater than or equal to Start"
                    else -> null
                }
            } else null
        }
    }
    val isValid by remember(definition) {
        derivedStateOf {
            startIndexInput.isNotEmpty() && endIndexInput.isNotEmpty()
                && startIndexError == null && endIndexError == null
        }
    }
    //endregion

    val saveDefinition: () -> Unit = {
        val definitionToSave = ByteGroupDefinition(
            startIndexInput.toInt()..endIndexInput.toInt(),
            name.ifBlank { null },
        )
        onDefinitionSaved(definitionToSave)

        if (definition == null) {
            startIndexInput = ""
            endIndexInput = ""
            name = ""
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            if (definition == null) "New definition" else "Update definition",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = startIndexInput,
                onValueChange = { startIndexInput = it },
                label = { Text("Start") },
                isError = startIndexError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )

            OutlinedTextField(
                value = endIndexInput,
                onValueChange = { endIndexInput = it },
                label = { Text("End") },
                isError = endIndexError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name?") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { saveDefinition() }
                )
            )

            val buttonColors = ButtonDefaults.buttonColors()
            IconButton(
                content = {
                    if (definition == null) {
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
                onClick = saveDefinition,
                modifier = Modifier.offset(y = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ByteGroupDefinitionFormPreview() {
    WrapForPreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ByteGroupDefinitionForm(
                definition = null,
                onDefinitionSaved = {},
            )
            ByteGroupDefinitionForm(
                definition = ByteGroupDefinition(2..5, "Test Group"),
                onDefinitionSaved = {},
            )
        }
    }
}
