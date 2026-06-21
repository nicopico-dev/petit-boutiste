/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTextReplacement
import fr.nicopico.petitboutiste.robot.MainPanePart
import fr.nicopico.petitboutiste.robot.PtbRobot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DataEntryTest {

    @get:Rule
    val rule: ComposeContentTestRule = createComposeRule()

    private lateinit var ptbRobot: PtbRobot

    @Before
    fun setUp() {
        ptbRobot = PtbRobot(rule)
    }

    @Test
    fun `Data entry should be converted to the selected type`() {
        ptbRobot
            .on(MainPanePart) {
                assertEquals(DATA_TYPE_HEX, getSelectedInputType())
                dataInput.performTextReplacement("FF00")
            }
            .takeScreenshot("01 - HEX data entry")
            .on(MainPanePart) {
                setSelectedInputType(DATA_TYPE_BIN)
                assertEquals(DATA_TYPE_BIN, getSelectedInputType())

                // Data entry is converted to BIN format
                dataInput.assertTextEquals("1111111100000000")
            }
            .takeScreenshot("02 - BIN conversion")
            .on(MainPanePart) {
                setSelectedInputType(DATA_TYPE_BASE64)
                assertEquals(DATA_TYPE_BASE64, getSelectedInputType())

                // Data entry is converted to BASE64 format
                dataInput.assertTextEquals("/wA=")
            }
            .takeScreenshot("03 - BASE64 conversion")
    }
}
