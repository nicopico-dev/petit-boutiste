/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.repository

import com.russhwolf.settings.MapSettings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BaseRepositorySettingsTest {

    @Serializable
    data class TestData(val name: String, val age: Int)

    private class TestRepositorySettings(
        settings: MapSettings,
        json: Json = Json.Default
    ) : BaseRepositorySettings(settings, json) {
        inline fun <reified T : Any> testEncodeAndStore(key: String, value: T) = encodeAndStore(key, value)
        inline fun <reified T : Any> testDecodeOrNull(key: String): T? = decodeOrNull<T>(key)
        fun testGetStringOrNull(key: String): String? = getStringOrNull(key)
        fun testPutStringOrRemove(key: String, value: String?) = putStringOrRemove(key, value)
    }

    private val settings = MapSettings()
    private val repository = TestRepositorySettings(settings)

    @Test
    fun `encodeAndStore saves data as JSON string`() {
        // Given
        val data = TestData("John", 30)
        val key = "test_key"

        // When
        repository.testEncodeAndStore(key, data)

        // Then
        val storedString = settings.getStringOrNull(key)
        assertEquals("""{"name":"John","age":30}""", storedString)
    }

    @Test
    fun `decodeOrNull restores data from JSON string`() {
        // Given
        val key = "test_key"
        settings.putString(key, """{"name":"Jane","age":25}""")

        // When
        val restored = repository.testDecodeOrNull<TestData>(key)

        // Then
        assertEquals(TestData("Jane", 25), restored)
    }

    @Test
    fun `decodeOrNull returns null for missing key`() {
        // When
        val restored = repository.testDecodeOrNull<TestData>("missing_key")

        // Then
        assertNull(restored)
    }

    @Test
    fun `decodeOrNull returns null and clears data for invalid JSON`() {
        // Given
        val key = "invalid_key"
        settings.putString(key, "{ invalid json }")

        // When
        val restored = repository.testDecodeOrNull<TestData>(key)

        // Then
        assertNull(restored)
        assertNull(settings.getStringOrNull(key))
    }

    @Test
    fun `putStringOrRemove removes key when value is null`() {
        // Given
        val key = "to_remove"
        settings.putString(key, "some value")

        // When
        repository.testPutStringOrRemove(key, null)

        // Then
        assertNull(settings.getStringOrNull(key))
    }
}
