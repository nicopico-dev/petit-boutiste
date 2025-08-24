package fr.nicopico.petitboutiste.repository

import com.russhwolf.settings.Settings
import fr.nicopico.petitboutiste.logError
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Base class to help repositories persist a single JSON blob using `Settings`.
 * Provides safe (de)serialization helpers with error handling.
 */
abstract class BaseRepositorySettings(
    protected val settings: Settings,
    protected val json: Json,
) {
    protected fun getStringOrNull(key: String): String? = try {
        settings.getStringOrNull(key)
    } catch (e: Throwable) {
        logError("Error reading settings key '$key': $e")
        null
    }

    protected fun putStringOrRemove(key: String, value: String?) {
        try {
            if (value == null) settings.remove(key) else settings.putString(key, value)
        } catch (e: Throwable) {
            logError("Error writing settings key '$key': $e")
        }
    }

    protected inline fun <reified T> decodeOrNull(key: String): T? {
        val data = getStringOrNull(key) ?: return null
        return try {
            json.decodeFromString<T>(data)
        } catch (e: SerializationException) {
            logError("Error decoding settings key '$key', clearing data ($e)")
            putStringOrRemove(key, null)
            null
        } catch (e: IllegalArgumentException) {
            // For cases where constructors invariants throw (e.g., HexString), clear and return null
            logError("Invalid persisted data for key '$key', clearing data ($e)")
            putStringOrRemove(key, null)
            null
        }
    }

    protected inline fun <reified T> encodeAndStore(key: String, value: T) {
        val data = json.encodeToString(value)
        putStringOrRemove(key, data)
    }
}
