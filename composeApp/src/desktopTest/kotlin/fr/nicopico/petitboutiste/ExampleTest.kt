/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import fr.nicopico.petitboutiste.robot.DataEntry
import fr.nicopico.petitboutiste.robot.PtbRobot
import org.junit.Rule
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class ExampleTest {

    @get:Rule
    val rule: ComposeContentTestRule = createComposeRule()

    @Test
    fun myTest(){
        // Declares a mock UI to demonstrate API calls
        //
        // Replace with your own declarations to test the code in your project
        rule.setContent {
            var text by remember { mutableStateOf("Hello") }

            Text(
                text = text,
                modifier = Modifier.testTag("text")
            )
            Button(
                onClick = { text = "Compose" },
                modifier = Modifier.testTag("button")
            ) {
                Text("Click me")
            }
        }

        // Tests the declared UI with assertions and actions of the JUnit-based testing API
        rule.onNodeWithTag("text").assertTextEquals("Hello")
        rule.onNodeWithTag("button").performClick()
        rule.onNodeWithTag("text").assertTextEquals("Compose")
    }

    @Test
    fun ptbTest() {
        PtbRobot(rule, screenshotFolder = File("/Users/nicopico/Downloads/ptb-ui-tests"))
            .on(DataEntry) {
                assertEquals(DATA_TYPE_HEX, getSelectedDataType())
                takeScreenshot()

                selectDataType(DATA_TYPE_BIN)
                assertEquals(DATA_TYPE_BIN, getSelectedDataType())
            }
            .takeScreenshot()
    }

    //endregion
}
