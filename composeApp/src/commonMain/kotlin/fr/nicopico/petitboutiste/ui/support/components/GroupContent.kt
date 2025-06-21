package fr.nicopico.petitboutiste.ui.support.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.RepresentationFormat
import fr.nicopico.petitboutiste.models.getRepresentation
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupContent(
    byteGroup: ByteItem.Group,
    modifier: Modifier = Modifier,
) {
    val representations = remember(byteGroup) {
        mapOf(
            "Hexadecimal" to (byteGroup.getRepresentation(RepresentationFormat.Hexadecimal) ?: "[ERROR]"),
            "Decimal" to (byteGroup.getRepresentation(RepresentationFormat.Decimal()) ?: "[ERROR]"),
            "Text" to (byteGroup.getRepresentation(RepresentationFormat.Text()) ?: "[ERROR]"),
        )
    }

    Column(modifier) {
        Text(byteGroup.name ?: "UNNAMED GROUP", style = MaterialTheme.typography.labelLarge)

        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(representations.entries.toList()) { (label, value) ->
                OutlinedTextField(
                    label = { Text(label) },
                    value = value,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun GroupContentPreview() {
    WrapForPreview {
        GroupContent(
            ByteItem.Group(
                bytes = listOf(
                    ByteItem.Single("62"),
                    ByteItem.Single("6F"),
                    ByteItem.Single("6E"),
                    ByteItem.Single("6A"),
                    ByteItem.Single("6F"),
                    ByteItem.Single("75"),
                    ByteItem.Single("72"),
                ),
                name = "Test"
            )
        )
    }
}
