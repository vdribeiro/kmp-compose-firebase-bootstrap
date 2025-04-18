package com.example.bootstrap.toggle

import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.config.remoteConfig

internal class FeatureFlag: FeatureToggle {

    override fun getBoolean(key: ToggleKey): Boolean? =
        Firebase.remoteConfig.getValue(key = key.key)?.asBoolean()

    override fun getString(key: ToggleKey): String? =
        Firebase.remoteConfig.getValue(key = key.key)?.asString()

    override fun getLong(key: ToggleKey): Long? =
        Firebase.remoteConfig.getValue(key = key.key)?.asLong()

    override fun getDouble(key: ToggleKey): Double? =
        Firebase.remoteConfig.getValue(key = key.key)?.asDouble()

    override fun getByteArray(key: ToggleKey): ByteArray? =
        Firebase.remoteConfig.getValue(key = key.key)?.asByteArray()
}
