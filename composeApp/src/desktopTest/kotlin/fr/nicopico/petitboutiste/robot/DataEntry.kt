/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.robot

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import fr.nicopico.petitboutiste.ui.UiTags

object DataEntry : PartRobot {

    const val DATA_TYPE_HEX = "HEX"
    const val DATA_TYPE_BIN = "BIN"
    const val DATA_TYPE_BASE64 = "B64"

    context(rule: ComposeContentTestRule)
    fun getSelectedDataType(): String {
        val controlNode = rule.onNodeWithTag(UiTags.INPUT_TYPE_TOGGLE)

        val selectedNode = controlNode
            .onChild() // SelectableGroup (Jewel)
            .onChildren()
            .filterToOne(isSelected())

        return selectedNode.fetchSemanticsNode()
            .config
            .getOrNull(SemanticsProperties.Text)
            ?.firstOrNull()
            ?.text.orEmpty()
    }

    context(rule: ComposeContentTestRule)
    fun selectDataType(dataType: String) {
        val controlNode = rule.onNodeWithTag(UiTags.INPUT_TYPE_TOGGLE)

        controlNode
            .onChild() // SelectableGroup (Jewel)
            .onChildren()
            .filterToOne(hasText(dataType))
            .performClick()

        rule.mainClock.advanceTimeByFrame()
    }
}
