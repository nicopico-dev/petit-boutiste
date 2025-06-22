package fr.nicopico.petitboutiste.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.Endianness
import fr.nicopico.petitboutiste.models.RepresentationFormat
import fr.nicopico.petitboutiste.models.extensions.getRepresentation
import fr.nicopico.petitboutiste.models.extensions.name
import fr.nicopico.petitboutiste.ui.infra.preview.WrapForPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ByteItemContent(
    byteItem: ByteItem,
    modifier: Modifier = Modifier,
) {
    var useBigEndian by remember {
        mutableStateOf(true)
    }
    val endianness by remember {
        derivedStateOf {
            if (useBigEndian) Endianness.BigEndian else Endianness.LittleEndian
        }
    }
    var collapsed by remember {
        mutableStateOf(false)
    }

    val representations = remember(byteItem, endianness) {
        mapOf(
            "Hexadecimal" to (byteItem.getRepresentation(RepresentationFormat.Hexadecimal) ?: "[ERROR]"),
            "Integer" to (byteItem.getRepresentation(RepresentationFormat.Integer(endianness)) ?: "[ERROR]"),
            "Text" to (byteItem.getRepresentation(RepresentationFormat.Text(endianness)) ?: "[ERROR]"),
        )
    }

    Column(modifier) {
        Row(
            Modifier.clickable {
                collapsed = !collapsed
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                (byteItem.name ?: "[UNNAMED]") + " Content",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                if (collapsed) Icons.Default.UnfoldMore else Icons.Default.UnfoldLess,
                "Toggle",
                modifier = Modifier.size(18.dp)
            )
        }

        if (!collapsed) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Big Endian ?")
                Checkbox(
                    useBigEndian,
                    onCheckedChange = { useBigEndian = it },
                    modifier = Modifier.padding(0.dp)
                )
            }

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
}

@Preview
@Composable
private fun GroupContentPreview() {
    WrapForPreview {
        ByteItemContent(
            ByteItem.Group(
                index = 0,
                bytes = "626F6E6A6F7572",
                name = "Test"
            )
        )
    }
}
