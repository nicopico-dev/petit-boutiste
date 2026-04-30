/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Representation
import io.github.vinceglb.filekit.utils.toKotlinxIoPath
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TemplateManagerImplTest {

    private val json = Json {
        allowStructuredMapKeys = true
        prettyPrint = true
    }
    private val templateManager = TemplateManagerImpl(json)
    private lateinit var tempDir: File

    @BeforeTest
    fun setup() {
        tempDir = Files.createTempDirectory("template-manager-test").toFile()
    }

    @AfterTest
    fun teardown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `saves and loads a template`() = runTest {
        // Given
        val template = Template(
            name = "Test Template",
            definitions = listOf(
                ByteGroupDefinition(0..1, "First Group")
            ),
            scratchpad = "Notes"
        )
        val templateFile = File(tempDir, "test.pbt")
        val templateFilePath = templateFile.toKotlinxIoPath()

        // When
        templateManager.saveTemplate(template, templateFilePath)
        val loaded = templateManager.loadTemplate(templateFilePath)

        // Then
        assertEquals(template.name, loaded.name)
        assertEquals(template.definitions.size, loaded.definitions.size)
        assertEquals(template.definitions.first().name, loaded.definitions.first().name)
        assertEquals(template.scratchpad, loaded.scratchpad)
    }

    @Test
    fun `saves template with relative paths for FileType arguments`() = runTest {
        // Given
        val subDir = File(tempDir, "templates").apply { mkdirs() }
        val externalFile = File(tempDir, "external.bin")
        externalFile.writeText("content")

        val template = Template(
            name = "Template with File",
            definitions = listOf(
                ByteGroupDefinition(
                    indexes = 0..1,
                    representation = Representation(
                        dataRenderer = DataRenderer.SubTemplate,
                        argumentValues = mapOf("templateFile" to externalFile.absolutePath)
                    )
                )
            )
        )
        val templateFile = File(subDir, "main.pbt")
        val templateFilePath = templateFile.toKotlinxIoPath()

        // When
        templateManager.saveTemplate(template, templateFilePath)

        // Then
        // Verify the file content contains the relative path
        val content = templateFile.readText()
        assertTrue(content.contains("../external.bin"), "Should contain relative path to external.bin, but was:\n$content")

        // When loading back
        val loaded = templateManager.loadTemplate(templateFilePath)

        // Then
        val loadedFilePath = loaded.definitions.first().representation.argumentValues["templateFile"]
        assertEquals(externalFile.absolutePath, loadedFilePath)
    }

    @Test
    fun `saveTemplate overwrites existing file when requested`() = runTest {
        // Given
        val templateFile = File(tempDir, "test.pbt")
        val templateFilePath = templateFile.toKotlinxIoPath()
        templateFile.writeText("original content")
        val template = Template(name = "New Template")

        // When
        templateManager.saveTemplate(template, templateFilePath, overwrite = true)

        // Then
        val loaded = templateManager.loadTemplate(templateFilePath)
        assertEquals("New Template", loaded.name)
    }
}
