/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.asString
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ByteItemExtTest {

    @Test
    fun renderWithReturnsNoneWhenRepresentationNotReady() = runTest {
        // Given
        val byteItem = SingleByte(index = 0, value = "AA")
        // SubTemplate requires a templateFile argument, so without it, isReady is false
        val representation = Representation(
            dataRenderer = DataRenderer.SubTemplate,
            argumentValues = emptyMap()
        )

        // When
        val result = byteItem.renderWith(representation)

        // Then
        assertIs<RenderResult.None>(result)
    }

    @Test
    fun renderWithUsesCachedRenderingForByteGroupWhenRepresentationMatches() = runTest {
        // Given
        val definition = ByteGroupDefinition.createFromRange(
            indexes = 0..1,
            representation = Representation(dataRenderer = DataRenderer.Hexadecimal)
        )
        val byteGroup = ByteGroup(
            definition = definition,
            bytes = listOf("AA", "BB"),
            startIndex = 0,
            endIndex = 1
        )
        val representation = byteGroup.definition.representation

        // When
        val result = byteGroup.renderWith(representation)

        // Then
        assertIs<RenderResult.Success>(result)
        assertEquals("AA BB", result.asString())
    }

    @Test
    fun renderWithRendersDirectlyWhenByteGroupRepresentationDoesNotMatch() = runTest {
        // Given
        val definition = ByteGroupDefinition.createFromRange(
            indexes = 0..1,
            representation = Representation(dataRenderer = DataRenderer.Hexadecimal)
        )
        val byteGroup = ByteGroup(
            definition = definition,
            bytes = listOf("AA", "BB"),
            startIndex = 0,
            endIndex = 1
        )
        val differentRepresentation = Representation(dataRenderer = DataRenderer.Binary)

        // When
        val result = byteGroup.renderWith(differentRepresentation)

        // Then
        assertIs<RenderResult.Success>(result)
        // Binary representation of AABB with spaces: 0xAA = 1010 1010, 0xBB = 1011 1011
        assertEquals("1010 1010 1011 1011", result.asString())
    }

    @Test
    fun renderWithRendersSingleByteDirectly() = runTest {
        // Given
        val byteItem = SingleByte(index = 0, value = "AA")
        val representation = Representation(dataRenderer = DataRenderer.Hexadecimal)

        // When
        val result = byteItem.renderWith(representation)

        // Then
        assertIs<RenderResult.Success>(result)
        assertEquals("AA", result.asString())
    }
}
