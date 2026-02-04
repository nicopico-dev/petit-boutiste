/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation.modifier

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun Modifier.clickableWithIndication(
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.clickable(
        interactionSource = interactionSource,
        indication = remember { InkIndication },
        onClick = onClick
    )
}

private object InkIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatingNode {
        return InkIndicationNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = System.identityHashCode(this)
}

private class InkIndicationNode(
    private val interactionSource: InteractionSource
) : DelegatingNode(), DrawModifierNode {
    private var isPressed: Boolean = false

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> isPressed = true
                    is HoverInteraction.Exit -> isPressed = false
                    is PressInteraction.Press -> isPressed = true
                    is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
                }
                invalidateDraw()
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (isPressed) {
            drawRect(color = Color.White.copy(alpha = 0.1f), size = size)
        }
    }
}
