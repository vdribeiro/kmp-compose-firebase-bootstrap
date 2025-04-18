package com.example.bootstrap.firebase.config

import com.example.bootstrap.firebase.Firebase

/**
 * Entry point of Firebase Remote Config.
 */
internal expect val Firebase.remoteConfig: RemoteConfig

internal expect class RemoteConfig {
    /**
     * All remote config values.
     */
    val all: Map<String, RemoteConfigValue>

    /**
     * Set the [RemoteConfigSettings].
     */
    suspend fun settings(settings: RemoteConfigSettings): RemoteConfig

    /**
     * Set the [defaults] representing the Firebase Remote Config parameter keys and values.
     * The values must be one of the following types:
     * - Boolean
     * - String
     * - Long
     * - Double
     * - byte[]
     */
    suspend fun setDefaults(defaults: Map<String, Any?>): RemoteConfig

    /**
     * Fetch and then activate the fetched configs.
     * If the time elapsed since the last fetch from the Firebase Remote Config backend is more
     * than the default minimum fetch interval, configs are fetched from the backend.
     * After the fetch is complete, the configs are activated so that the fetched key value pairs take effect.
     *
     * Returns true if the current call activated the fetched configs;
     * false if no configs were fetched from the backend and the local fetched configs have already been activated.
     */
    suspend fun fetchAndActivate(): Boolean

    /**
     * Get the parameter value for the given [key].
     */
    fun getValue(key: String): RemoteConfigValue?
}

internal data class RemoteConfigSettings(
    /**
     * The minimum interval between successive fetch calls in seconds. Should be a non-negative number.
     */
    val minimumFetchIntervalInSeconds: Long,
    /**
     * Connection and read timeout in seconds for fetch requests. Should be a non-negative number.
     * A fetch call will fail if it takes longer than the specified timeout.
     */
    val fetchTimeoutInSeconds: Long
)

/**
 * Wrapper for a Remote Config parameter value, with methods to get it as different types.
 */
internal expect class RemoteConfigValue {
    fun asBoolean(): Boolean?
    fun asString(): String?
    fun asLong(): Long?
    fun asDouble(): Double?
    fun asByteArray(): ByteArray?
}
