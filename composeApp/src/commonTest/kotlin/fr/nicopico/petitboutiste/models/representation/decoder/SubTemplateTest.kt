/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.definition.ByteGroupDefinition
import fr.nicopico.petitboutiste.models.persistence.Template
import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.repository.TemplateManager
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SubTemplateTest {

    private val templateManager = TemplateManager()

    @Test
    fun `decodeSubTemplate should return JSON mapping of named groups`() = runTest {
        // GIVEN
        val template = Template(
            name = "Test Template",
            definitions = listOf(
                ByteGroupDefinition(indexes = 0..1, name = "first", representation = Representation(DataRenderer.Integer)),
                ByteGroupDefinition(indexes = 2..3, name = "second", representation = Representation(DataRenderer.Hexadecimal))
            )
        )
        val templateFile = File.createTempFile("test_template", ".ptb").apply {
            templateManager.saveTemplate(template, this, overwrite = true)
            deleteOnExit()
        }

        val payload = byteArrayOf(0x00, 0x01, 0x02, 0x03)
        val argumentValues = mapOf("templateFile" to templateFile.absolutePath)

        // WHEN
        val result = DataRenderer.SubTemplate.decodeSubTemplate(payload, argumentValues)

        // THEN
        // decodeInteger(00 01) -> "1"
        // decodeHexadecimal(02 03) -> "0203"
        val expected = """{"first":"1","second":"0203"}"""
        assertEquals(expected, result)
    }

    @Test
    fun `decodeSubTemplate should throw error when a group rendering fails`() = runTest {
        // GIVEN
        val template = Template(
            name = "Test Template",
            definitions = listOf(
                ByteGroupDefinition(
                    indexes = 0..1,
                    name = "errorGroup",
                    // Protobuf will fail if the file does not exist
                    representation = Representation(
                        DataRenderer.Protobuf,
                        mapOf(
                            "protoFile" to "non_existent_file.desc",
                            "messageType" to "SomeMessage"
                        )
                    )
                )
            )
        )
        val templateFile = File.createTempFile("test_template_error", ".ptb").apply {
            templateManager.saveTemplate(template, this, overwrite = true)
            deleteOnExit()
        }

        val payload = byteArrayOf(0x00, 0x01)
        val argumentValues = mapOf("templateFile" to templateFile.absolutePath)

        // WHEN & THEN
        assertFailsWith<IllegalStateException> {
            DataRenderer.SubTemplate.decodeSubTemplate(payload, argumentValues)
        }
    }

    @Test
    fun `getSubTemplateFile should return the file from arguments`() {
        // GIVEN
        val file = File("some/path/template.ptb")
        val representation = Representation(
            dataRenderer = DataRenderer.SubTemplate,
            argumentValues = mapOf("templateFile" to file.absolutePath)
        )

        // WHEN
        val result = representation.getSubTemplateFile()

        // THEN
        assertEquals(file.absolutePath, result?.absolutePath)
    }

    @Test
    fun `getSubTemplateDefinitions should return definitions from file`() = runTest {
        // GIVEN
        val definitions = listOf(
            ByteGroupDefinition(indexes = 0..1, name = "first")
        )
        val template = Template(name = "Test", definitions = definitions)
        val templateFile = File.createTempFile("test_template_defs", ".ptb").apply {
            templateManager.saveTemplate(template, this, overwrite = true)
            deleteOnExit()
        }
        val representation = Representation(
            dataRenderer = DataRenderer.SubTemplate,
            argumentValues = mapOf("templateFile" to templateFile.absolutePath)
        )

        // WHEN
        val result = representation.getSubTemplateDefinitions()

        // THEN
        assertEquals(definitions, result)
    }

    @Test
    fun `getSubTemplateDefinitions should return empty list if file is missing`() {
        // GIVEN
        val representation = Representation(
            dataRenderer = DataRenderer.SubTemplate,
            argumentValues = emptyMap()
        )

        // WHEN
        val result = representation.getSubTemplateDefinitions()

        // THEN
        assertEquals(emptyList(), result)
    }
}
