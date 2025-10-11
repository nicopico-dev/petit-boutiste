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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.models.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.ByteItem
import fr.nicopico.petitboutiste.models.representation.isOff
import fr.nicopico.petitboutiste.models.representation.isReady
import fr.nicopico.petitboutiste.models.representation.renderAsString
import fr.nicopico.petitboutiste.ui.theme.JewelThemeUtils
import fr.nicopico.petitboutiste.utils.compose.Slot
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.typography

@Composable
fun ByteGroupDefinitionItem(
    definition: ByteGroupDefinition,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    byteGroup: ByteItem.Group? = null,
    invalidDefinition: Boolean = false,
    form: Slot? = null,
) {
    val incomplete = byteGroup?.incomplete ?: false
    var displayForm by remember(definition) {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (incomplete || invalidDefinition) {
                    JewelThemeUtils.colors.errorColor
                } else JewelThemeUtils.colors.borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .background(if (selected) JewelThemeUtils.colors.accentContainer else Color.Transparent)
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
                )

                val rangeSuffix = with(definition.indexes) {
                    val rangeText = when {
                        invalidDefinition -> ") - invalid start index"
                        incomplete -> ", incomplete)"
                        else -> ")"
                    }
                    "$start..$endInclusive (${count()} bytes$rangeText"
                }
                Text(
                    text = rangeSuffix,
                    style = JewelTheme.typography.medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                val valueText = if (
                    byteGroup != null
                    && !definition.representation.isOff
                    && definition.representation.isReady
                ) {
                    definition.representation.renderAsString(byteGroup)
                } else null
                if (valueText != null) {
                    Text(
                        text = valueText,
                        style = JewelTheme.typography.consoleTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            IconButton(
                content = {
                    Icon(
                        key = AllIconsKeys.General.Delete,
                        contentDescription = "Remove byte group",
                        tint = JewelThemeUtils.colors.dangerousActionColor,
                    )
                },
                onClick = onDelete,
                modifier = Modifier.align(Alignment.CenterVertically),
            )

            if (form != null) {
                Spacer(Modifier.width(4.dp))

                IconButton(
                    content = {
                        Icon(
                            key = AllIconsKeys.General.ArrowDownSmall,
                            contentDescription = "Toggle form",
                            modifier = Modifier.rotate(if (displayForm) 180f else 0f)
                        )
                    },
                    onClick = {
                        displayForm = !displayForm
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
