/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.robot

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.ui.UiTags

object RepresentationPart : PartRobot {

    context(rule: ComposeContentTestRule)
    fun setRenderer(renderer: DataRenderer) {
        rule.onNodeWithTag(UiTags.BYTE_GROUP_REPRESENTATION_FORM_DATA_RENDERER)
            .performClick()

        // Selection in PBDropdown
        // Use onLast() to pick the item in the popup instead of the dropdown itself
        rule.onAllNodes(hasText(renderer.label), useUnmergedTree = true)
            .onLast()
            .performClick()
    }

    context(rule: ComposeContentTestRule)
    fun setChoiceArgument(key: String, label: String) {
        rule.onNodeWithTag(UiTags.argumentInput(key))
            .performClick()

        // Use onLast() to pick the item in the popup instead of the dropdown itself
        rule.onAllNodes(hasText(label), useUnmergedTree = true)
            .onLast()
            .performClick()
    }

    context(rule: ComposeContentTestRule)
    fun setNumericArgument(key: String, value: String) {
        rule.onNodeWithTag(UiTags.argumentInput(key))
            .performTextInput(value)
    }

    context(rule: ComposeContentTestRule)
    fun verifyDecodedOutput(expected: String) {
        rule.onNode(
            hasAnyAncestor(hasNodeWithTag(UiTags.BYTE_ITEM_RENDER))
                and hasNodeWithTag(UiTags.BYTE_GROUP_REPRESENTATION_RENDER)
        ).assertTextEquals(expected)
    }

    private fun hasNodeWithTag(tag: String) = androidx.compose.ui.test.hasTestTag(tag)
}
