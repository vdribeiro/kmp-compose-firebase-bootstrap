package com.example.bootstrap.toggle

internal interface FeatureToggle {

    /**
     * Get boolean toggle or null if it does not exist.
     */
    fun getBoolean(key: ToggleKey): Boolean?

    /**
     * Get String toggle or null if it does not exist.
     */
    fun getString(key: ToggleKey): String?

    /**
     * Get Long toggle or null if it does not exist.
     */
    fun getLong(key: ToggleKey): Long?

    /**
     * Get Double toggle or null if it does not exist.
     */
    fun getDouble(key: ToggleKey): Double?

    /**
     * Get Byte Array toggle or null if it does not exist.
     */
    fun getByteArray(key: ToggleKey): ByteArray?
}
