/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.robot

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import fr.nicopico.petitboutiste.ui.UiTags

object DefinitionsPart : PartRobot {

    context(rule: ComposeContentTestRule)
    fun addDefinition(
        name: String,
        start: Int,
        end: Int,
    ) {
        rule.onNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ADD_DEFINITION)
            .performClick()

        rule.onNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_NAME)
            .apply {
                performTextClearance()
                performTextInput(name)
            }

        rule.onNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_START)
            .apply {
                performTextClearance()
                performTextInput(start.toString())
            }

        rule.onNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_END)
            .apply {
                performTextClearance()
                performTextInput(end.toString())
            }

        rule.onNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_SAVE)
            .performClick()
    }

    context(rule: ComposeContentTestRule)
    fun toggleForm(name: String) {
        rule.onNode(
            hasParent(
                hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM)
                    and hasAnyChild(hasNodeWithTag(UiTags.byteGroupDefinitionsItemName(name)))
            ) and hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_TOGGLE_FORM),
            useUnmergedTree = true,
        ).performClick()
    }

    context(rule: ComposeContentTestRule)
    fun selectDefinition(name: String) {
        rule.onNodeWithTag(UiTags.byteGroupDefinitionsItemName(name), useUnmergedTree = true)
            .performClick()
        rule.waitForIdle()
    }

    context(rule: ComposeContentTestRule)
    fun verifyError(name: String, expectedError: String) {
        rule.onNode(
            hasAnyAncestor(
                hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM)
                    and hasAnyChild(hasNodeWithTag(UiTags.byteGroupDefinitionsItemName(name)))
            ) and hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_ERROR)
        ).assertTextEquals(expectedError)
    }

    private fun hasNodeWithTag(tag: String) = hasTestTag(tag)
}
