/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ProtobufDecoderTest {

    @Test
    fun `should decode protobuf message using descriptor set`() = runTest {
        // GIVEN
        val descriptorHex = "0a770a1774656d705f70726f746f2f706572736f6e2e70726f746f121066722e6e69636f7069636f2e7465737422420a06506572736f6e12120a046e616d6518012001280952046e616d65120e0a0269641802200128055202696412140a05656d61696c1803200128095205656d61696c620670726f746f33"
        val descriptorFile = File.createTempFile("person", ".desc").apply {
            writeBytes(descriptorHex.hexToByteArray())
            deleteOnExit()
        }

        val messageHex = "0a084a6f686e20446f65107b1a106a6f686e406578616d706c652e636f6d"
        val payload = messageHex.hexToByteArray()

        val argumentValues = mapOf(
            "protoFile" to descriptorFile.absolutePath,
            "messageType" to "Person"
        )

        // WHEN
        val result = DataRenderer.Protobuf.decodeProtobuf(payload, argumentValues)

        // THEN
        val expectedJson = """
            {
              "name": "John Doe",
              "id": 123,
              "email": "john@example.com"
            }
        """.trimIndent()

        assertEquals(expectedJson, result)
    }
}
