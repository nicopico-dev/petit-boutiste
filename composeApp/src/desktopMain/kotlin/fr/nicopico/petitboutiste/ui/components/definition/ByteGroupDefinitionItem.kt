/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.definition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import fr.nicopico.petitboutiste.models.definition.ByteGroup
import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.representation.asString
import fr.nicopico.petitboutiste.models.representation.isOff
import fr.nicopico.petitboutiste.ui.UiTags
import fr.nicopico.petitboutiste.ui.UiTags.BYTE_GROUP_DEFINITIONS_ITEM_ERROR
import fr.nicopico.petitboutiste.ui.UiTags.BYTE_GROUP_DEFINITIONS_ITEM_RANGE
import fr.nicopico.petitboutiste.ui.UiTags.BYTE_GROUP_DEFINITIONS_ITEM_REMOVE
import fr.nicopico.petitboutiste.ui.UiTags.BYTE_GROUP_DEFINITIONS_ITEM_RENDER
import fr.nicopico.petitboutiste.ui.UiTags.BYTE_GROUP_DEFINITIONS_ITEM_TOGGLE_FORM
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.utils.compose.Slot
import fr.nicopico.petitboutiste.utils.size
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.typography

@Composable
fun ByteGroupDefinitionItem(
    definition: ByteGroupDefinition,
    onToggleDisplayForm: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    byteGroup: ByteGroup? = null,
    errorMessage: String? = null,
    form: Slot? = null,
    displayForm: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { selected = isSelected }
            .border(
                width = 1.dp,
                color = if (errorMessage != null) {
                    AppTheme.current.colors.errorColor
                } else AppTheme.current.colors.borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .background(if (isSelected) AppTheme.current.colors.accentContainer else Color.Transparent)
            .padding(16.dp)
    ) {
        Row {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    definition.name ?: "[UNNAMED]",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .testTag(UiTags.byteGroupDefinitionsItemName(definition.name))
                )

                val rangeSuffix = with(definition.indexes) {
                    "$start..$endInclusive ($size bytes)"
                }
                Text(
                    text = rangeSuffix,
                    style = JewelTheme.typography.medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag(BYTE_GROUP_DEFINITIONS_ITEM_RANGE)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = JewelTheme.typography.medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag(BYTE_GROUP_DEFINITIONS_ITEM_ERROR)
                    )
                }

                var valueText: String? by remember {
                    mutableStateOf(null)
                }
                LaunchedEffect(byteGroup, definition.representation) {
                    valueText = if (
                        byteGroup != null
                        && !definition.representation.isOff
                        && definition.representation.isReady
                    ) {
                        byteGroup.getOrComputeRendering().asString(
                            singleLine = true
                        )
                    } else null
                }

                valueText?.let { valueText ->
                    Text(
                        text = valueText,
                        style = JewelTheme.typography.consoleTextStyle,
                        fontSize = 1.em,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag(BYTE_GROUP_DEFINITIONS_ITEM_RENDER)
                    )
                }
            }

            IconButton(
                content = {
                    Icon(
                        key = AllIconsKeys.General.Delete,
                        contentDescription = "Remove byte group",
                        tint = AppTheme.current.colors.dangerousActionColor,
                    )
                },
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .testTag(BYTE_GROUP_DEFINITIONS_ITEM_REMOVE),
            )

            if (form != null) {
                Spacer(Modifier.width(4.dp))

                IconButton(
                    content = {
                        Icon(
                            key = AllIconsKeys.General.ArrowDownSmall,
                            contentDescription = "Toggle form",
                            modifier = Modifier
                                .rotate(if (displayForm) 180f else 0f)
                                .testTag(BYTE_GROUP_DEFINITIONS_ITEM_TOGGLE_FORM)
                        )
                    },
                    onClick = {
                        onToggleDisplayForm(!displayForm)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }

        if (form != null && displayForm) {
            form()
        }
    }
}
