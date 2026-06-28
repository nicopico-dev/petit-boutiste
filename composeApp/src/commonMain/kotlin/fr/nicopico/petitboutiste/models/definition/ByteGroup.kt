/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.representation.DEFAULT_REPRESENTATION
import fr.nicopico.petitboutiste.models.representation.RenderResult
import fr.nicopico.petitboutiste.models.representation.Representation
import fr.nicopico.petitboutiste.models.representation.render
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// TODO NPI rename firstIndex to startIndex
// TODO NPI rename lastIndex to endIndex
data class ByteGroup(
    val bytes: List<String>,
    val definition: ByteGroupDefinition,
    override val firstIndex: Int,
    /**
     * If the group is incomplete, `lastIndex` will be the actual index of the groups last byte
     */
    override val lastIndex: Int = firstIndex + (bytes.count() - 1),
    /**
     * Set to `true` if [bytes] do not match the definition size.
     * This means the payload is likely incomplete or the definition is incorrect
     */
    val incomplete: Boolean = false,
) : ByteItem() {

    init {
        require(bytes.isNotEmpty()) {
            "bytes must not be empty"
        }
        require(bytes.all { it.length == 2 }) {
            "Each bytes must have a length of 2"
        }
    }

    val name: String? = definition.name

    private var _cachedRenderResult: RenderResult? = null
    private val renderMutex = Mutex()

    /**
     * Gets the cached rendering result or computes it on first access.
     * The rendering is computed using the definition's representation and cached for reuse.
     * Thread-safe lazy initialization using a mutex.
     */
    @Suppress("ReturnCount")
    suspend fun getOrComputeRendering(): RenderResult {
        // Fast path: return cached value if available
        _cachedRenderResult?.let { return it }

        // Slow path: compute and cache the rendering
        return renderMutex.withLock {
            // Double-check after acquiring lock
            _cachedRenderResult?.let { return it }

            val result = definition.representation.render(this)
            _cachedRenderResult = result
            result
        }
    }

    override fun toString(): String {
        return bytes.joinToString(separator = "")
    }

    companion object {
        fun fromRange(
            bytes: List<String>,
            indexes: IntRange,
            name: String? = null,
            representation: Representation = DEFAULT_REPRESENTATION,
        ) = ByteGroup(
            bytes = bytes,
            definition = ByteGroupDefinition.createFromRange(
                indexes = indexes,
                name = name,
                representation = representation,
            ),
            firstIndex = indexes.first(),
            lastIndex = indexes.last(),
        )

        /**
         * Utility method to create ByteGroup from a start index and a data payload.
         * This mimics the constructor of [SingleByte]
         */
        fun forPreview(
            index: Int,
            data: String,
            name: String? = null,
            representation: Representation = DEFAULT_REPRESENTATION,
        ): ByteGroup {
            return ByteGroup(
                bytes = data.windowed(2, 2),
                firstIndex = index,
                definition = ByteGroupDefinition.createFromRange(
                    indexes = index..<(index + (data.length / 2)),
                    name = name,
                    representation = representation,
                )
            )
        }
    }
}
