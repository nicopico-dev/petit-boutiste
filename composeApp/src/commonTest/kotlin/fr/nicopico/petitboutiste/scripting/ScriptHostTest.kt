/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.scripting

import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScriptHostTest {

    private val api = object : PetitBoutisteApi {
        val debugMessages = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()
        var currentPayload: ByteArray = byteArrayOf()

        override fun debug(message: String) {
            debugMessages.add(message)
        }

        override fun error(message: String) {
            errorMessages.add(message)
        }

        override fun getPayload(): ByteArray = currentPayload
    }

    private val scriptHost = ScriptHost(createApi = { api })

    @Test
    fun `evaluates a simple script`() = runTest {
        // Given
        val scriptFile = createTempScript("""
            ptb.debug("Hello from script!")
            ptb.debug("Args: " + args.joinToString())
        """.trimIndent())

        try {
            // When
            val result = scriptHost.evalFile(scriptFile, listOf("arg1", "arg2"))

            // Then
            assertTrue(result is ResultWithDiagnostics.Success, "Script evaluation failed: ${result.reports}")
            assertEquals(listOf("Hello from script!", "Args: arg1, arg2"), api.debugMessages)
        } finally {
            scriptFile.delete()
        }
    }

    @Test
    fun `script can access payload`() = runTest {
        // Given
        api.currentPayload = byteArrayOf(0x01, 0x02, 0x03)
        val scriptFile = createTempScript("""
            val payload = ptb.getPayload()
            ptb.debug("Payload size: " + payload.size)
        """.trimIndent())

        try {
            // When
            val result = scriptHost.evalFile(scriptFile)

            // Then
            assertTrue(result is ResultWithDiagnostics.Success, "Script evaluation failed: ${result.reports}")
            assertEquals(listOf("Payload size: 3"), api.debugMessages)
        } finally {
            scriptFile.delete()
        }
    }

    @Test
    fun `script execution error is reported`() = runTest {
        // Given
        val scriptFile = createTempScript("""
            throw java.lang.RuntimeException("Script execution error")
        """.trimIndent())

        try {
            // When
            val result = scriptHost.evalFile(scriptFile)

            // Then
            // Note: execution errors within the script may still return ResultWithDiagnostics.Success,
            // but with a ResultValue.Error as the returnValue.
            val returnValue = (result as? ResultWithDiagnostics.Success)?.value?.returnValue
            assertTrue(result is ResultWithDiagnostics.Failure || returnValue is ResultValue.Error,
                "Script evaluation should have failed, but was Success with returnValue: $returnValue")
        } finally {
            scriptFile.delete()
        }
    }

    @Test
    fun `script compilation error is reported`() = runTest {
        // Given
        val scriptFile = createTempScript("""
            invalid code
        """.trimIndent())

        try {
            // When
            val result = scriptHost.evalFile(scriptFile)

            // Then
            assertTrue(result is ResultWithDiagnostics.Failure, "Script evaluation should have failed compilation")
        } finally {
            scriptFile.delete()
        }
    }

    private fun createTempScript(content: String): File {
        return File.createTempFile("test-script", ".kts").apply {
            writeText(content)
            deleteOnExit()
        }
    }
}
