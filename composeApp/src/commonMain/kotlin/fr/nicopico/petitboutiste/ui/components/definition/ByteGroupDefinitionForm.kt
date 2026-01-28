/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabel
import fr.nicopico.petitboutiste.ui.components.foundation.PBLabelOrientation.Horizontal
import fr.nicopico.petitboutiste.ui.components.foundation.PBTextField
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreviewDesktop
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

private val fieldMaxWidth = 200.dp

@Composable
fun ByteGroupDefinitionForm(
    definition: ByteGroupDefinition,
    onDefinitionSaved: (ByteGroupDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    var startIndexInput by remember(definition.id) {
        mutableStateOf(definition.indexes.first.toString())
    }
    var endIndexInput by remember(definition.id) {
        mutableStateOf(definition.indexes.last.toString())
    }
    var name by remember(definition.id) {
        mutableStateOf(definition.name ?: "")
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
        val definitionToSave = definition.copy(
            indexes = startIndexInput.toInt()..endIndexInput.toInt(),
            name = name.ifBlank { null },
        )
        onDefinitionSaved(definitionToSave)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        PBLabel("Name (optional)", orientation = Horizontal) {
            PBTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.widthIn(max = fieldMaxWidth).fillMaxWidth(),
            )
        }

        PBLabel("Start", orientation = Horizontal) {
            PBTextField(
                value = startIndexInput,
                onValueChange = { startIndexInput = it },
                isError = startIndexError != null,
                modifier = Modifier.widthIn(max = fieldMaxWidth).fillMaxWidth(),
            )
        }

        PBLabel("End", orientation = Horizontal) {
            PBTextField(
                value = endIndexInput,
                onValueChange = { endIndexInput = it },
                isError = endIndexError != null,
                modifier = Modifier.widthIn(max = fieldMaxWidth).fillMaxWidth(),
            )
        }

        DefaultButton(
            content = { Text(text = "Save definition") },
            onClick = saveDefinition,
            enabled = isValid,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Preview
@Composable
private fun ByteGroupDefinitionFormPreview() {
    WrapForPreviewDesktop {
        ByteGroupDefinitionForm(
            definition = ByteGroupDefinition(2..5, "Test ByteGroup"),
            onDefinitionSaved = {},
        )
    }
}
