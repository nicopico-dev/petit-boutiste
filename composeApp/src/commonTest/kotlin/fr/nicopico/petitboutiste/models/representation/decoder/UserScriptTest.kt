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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UserScriptTest {

    @Test
    fun `decodeUserScript should return script result`() = runTest {
        // GIVEN
        val scriptContent = """
            "Hello from script!"
        """.trimIndent()
        val scriptFile = createTempScriptFile(scriptContent)
        val payload = byteArrayOf(0x01, 0x02)
        val argumentValues = mapOf("protoFile" to scriptFile.absolutePath)

        // WHEN
        val result = DataRenderer.UserScript.decodeUserScript(payload, argumentValues)

        // THEN
        assertEquals("Hello from script!", result)
    }

    @Test
    fun `decodeUserScript should provide access to payload via ptb`() = runTest {
        // GIVEN
        val scriptContent = """
            ptb.getPayload().joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0').uppercase() }
        """.trimIndent()
        val scriptFile = createTempScriptFile(scriptContent)
        val payload = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val argumentValues = mapOf("protoFile" to scriptFile.absolutePath)

        // WHEN
        val result = DataRenderer.UserScript.decodeUserScript(payload, argumentValues)

        // THEN
        assertEquals("DEADBEEF", result)
    }

    @Test
    fun `decodeUserScript should return error message when script returns Unit`() = runTest {
        // GIVEN
        val scriptContent = """
            val x = 1 + 1
            // returns Unit
        """.trimIndent()
        val scriptFile = createTempScriptFile(scriptContent)
        val payload = byteArrayOf()
        val argumentValues = mapOf("protoFile" to scriptFile.absolutePath)

        // WHEN
        val result = DataRenderer.UserScript.decodeUserScript(payload, argumentValues)

        // THEN
        assertEquals("ERROR: No return value", result)
    }

    @Test
    fun `decodeUserScript should return error message when script has compilation errors`() = runTest {
        // GIVEN
        val scriptContent = """
            invalid kotlin code
        """.trimIndent()
        val scriptFile = createTempScriptFile(scriptContent)
        val payload = byteArrayOf()
        val argumentValues = mapOf("protoFile" to scriptFile.absolutePath)

        // WHEN
        val result = DataRenderer.UserScript.decodeUserScript(payload, argumentValues)

        // THEN
        assertTrue(result.startsWith("ERROR: "), "Result should start with ERROR:, but was: $result")
        assertTrue(result.contains("Unresolved reference"), "Result should contain compilation error message")
    }

    @Test
    fun `decodeUserScript should return error message when script throws exception`() = runTest {
        // GIVEN
        val scriptContent = """
            throw RuntimeException("Something went wrong")
        """.trimIndent()
        val scriptFile = createTempScriptFile(scriptContent)
        val payload = byteArrayOf()
        val argumentValues = mapOf("protoFile" to scriptFile.absolutePath)

        // WHEN
        val result = DataRenderer.UserScript.decodeUserScript(payload, argumentValues)

        // THEN
        assertEquals("ERROR: java.lang.RuntimeException: Something went wrong", result)
    }

    @Test
    fun `decodeUserScript should throw exception when script file argument is missing`() = runTest {
        // GIVEN
        val payload = byteArrayOf()
        val argumentValues = emptyMap<String, String>()

        // WHEN & THEN
        assertFailsWith<IllegalArgumentException> {
            DataRenderer.UserScript.decodeUserScript(payload, argumentValues)
        }
    }

    @Test
    fun `decodeUserScript should return error message when script file does not exist`() = runTest {
        // GIVEN
        val payload = byteArrayOf()
        val argumentValues = mapOf("protoFile" to "non_existent_script.kts")

        // WHEN
        val result = DataRenderer.UserScript.decodeUserScript(payload, argumentValues)

        // THEN
        assertTrue(result.startsWith("ERROR: "), "Result should start with ERROR:, but was: $result")
    }

    private fun createTempScriptFile(content: String): File {
        return File.createTempFile("test_script", ".kts").apply {
            writeText(content)
            deleteOnExit()
        }
    }
}
