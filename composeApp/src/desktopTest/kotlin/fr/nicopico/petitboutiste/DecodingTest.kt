/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.robot.DefinitionsPart
import fr.nicopico.petitboutiste.robot.MainPanePart
import fr.nicopico.petitboutiste.robot.PtbRobot
import fr.nicopico.petitboutiste.robot.RepresentationPart
import org.junit.Rule
import org.junit.Test

class DecodingTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `decode multiple fields from a single payload`() {
        val robot = PtbRobot(rule)

        robot
            // 1. Initialize the app with a specific HEX payload
            .on(MainPanePart) {
                setSelectedInputType(DATA_TYPE_HEX)
                dataInput.performTextClearance()
                dataInput.performTextInput("010248656C6C6F00FF")
            }
            // 2. Add byte group definitions
            .on(DefinitionsPart) {
                addDefinition(name = "Field 1", start = 0, end = 1)
                addDefinition(name = "Field 2", start = 2, end = 6)
                addDefinition(name = "Field 3", start = 7, end = 8)
            }
            // 3. For each field: Select it and verify the decoded value
            .on(DefinitionsPart) { selectDefinition("Field 1") }
            .on(RepresentationPart) {
                setRenderer(DataRenderer.Integer)
                setChoiceArgument("endianness", "BigEndian")
                verifyDecodedOutput("258")
            }
            .on(DefinitionsPart) { selectDefinition("Field 2") }
            .on(RepresentationPart) {
                setRenderer(DataRenderer.Text)
                verifyDecodedOutput("Hello")
            }
            .on(DefinitionsPart) { selectDefinition("Field 3") }
            .on(RepresentationPart) {
                setRenderer(DataRenderer.Integer)
                setChoiceArgument("endianness", "BigEndian")
                verifyDecodedOutput("255")
            }
    }

    @Test
    fun `show error message for incomplete payload`() {
        val robot = PtbRobot(rule)

        robot
            // 1. Initialize the app with a specific HEX payload
            .on(MainPanePart) {
                setSelectedInputType(DATA_TYPE_HEX)
                dataInput.performTextClearance()
                dataInput.performTextInput("0102")
            }
            // 2. Add a definition that exceeds the payload
            .on(DefinitionsPart) {
                addDefinition(name = "Big Field", start = 0, end = 3) // 4 bytes, but only 2 available
            }
            // 3. Verify error message
            .on(DefinitionsPart) {
                verifyError("Big Field", "The payload is incomplete (2 bytes instead of 4)")
            }
    }

    @Test
    fun `handle empty payload gracefully`() {
        val robot = PtbRobot(rule)

        robot
            .on(MainPanePart) {
                setSelectedInputType(DATA_TYPE_HEX)
                dataInput.performTextClearance()
            }
            .on(DefinitionsPart) {
                addDefinition(name = "Field on empty", start = 0, end = 0)
                verifyNoError("Field on empty")
            }
    }
}
