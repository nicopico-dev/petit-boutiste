/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.robot

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import fr.nicopico.petitboutiste.ui.UiTags
import fr.nicopico.petitboutiste.utils.onPBTextFieldInput

@Suppress("unused")
object DefinitionsPart : PartRobot {

    context(rule: ComposeContentTestRule)
    fun addDefinition(
        name: String,
        start: Int,
        end: Int,
    ) {
        rule.onNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ADD_DEFINITION)
            .performClick()

        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithTag(
                UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_NAME,
                useUnmergedTree = true,
            ).fetchSemanticsNodes().isNotEmpty()
        }

        rule.onPBTextFieldInput(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_NAME)
            .apply {
                performTextClearance()
                performTextInput(name)
            }

        rule.onPBTextFieldInput(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_START)
            .apply {
                performTextClearance()
                performTextInput(start.toString())
            }

        rule.onPBTextFieldInput(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_FORM_INPUT_END)
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
        val definitionItem = rule.onNode(
            hasTestTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM) and
                hasAnyChild(hasTestTag(UiTags.byteGroupDefinitionsItemName(name))),
            useUnmergedTree = true,
        )

        val semantics = definitionItem.fetchSemanticsNode().config
        if (!semantics.contains(SemanticsProperties.Selected) || !semantics[SemanticsProperties.Selected]) {
            definitionItem.performClick()
        }

        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithTag(
                UiTags.BYTE_GROUP_REPRESENTATION_FORM_DATA_RENDERER,
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }

    context(rule: ComposeContentTestRule)
    fun verifyError(name: String, expectedError: String) {
        rule.onNode(
            hasAnyAncestor(
                hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM)
                    and hasAnyChild(hasNodeWithTag(UiTags.byteGroupDefinitionsItemName(name)))
            ) and hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_ERROR),
            useUnmergedTree = true,
        ).assertTextEquals(expectedError)
    }

    context(rule: ComposeContentTestRule)
    fun verifyNoError(name: String) {
        rule.onNode(
            hasAnyAncestor(
                hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM)
                    and hasAnyChild(hasNodeWithTag(UiTags.byteGroupDefinitionsItemName(name)))
            ) and hasNodeWithTag(UiTags.BYTE_GROUP_DEFINITIONS_ITEM_ERROR),
            useUnmergedTree = true,
        ).assertDoesNotExist()
    }

    private fun hasNodeWithTag(tag: String) = hasTestTag(tag)
}
