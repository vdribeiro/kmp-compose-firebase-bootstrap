package com.example.bootstrap.firebase.config

import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.tryAwait
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue

internal actual val Firebase.remoteConfig: RemoteConfig get() = RemoteConfig(remoteConfig = FirebaseRemoteConfig.getInstance())

internal actual class RemoteConfig(private val remoteConfig: FirebaseRemoteConfig) {
    actual val all: Map<String, RemoteConfigValue> get() = remoteConfig.all.mapValues { RemoteConfigValue(remoteConfigValue = it.value) }

    actual suspend fun settings(settings: RemoteConfigSettings): RemoteConfig = apply {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(settings.minimumFetchIntervalInSeconds)
                .setFetchTimeoutInSeconds(settings.fetchTimeoutInSeconds)
                .build()
        ).tryAwait()
    }

    actual suspend fun setDefaults(defaults: Map<String, Any?>): RemoteConfig = apply {
        remoteConfig.setDefaultsAsync(defaults).tryAwait()
    }

    actual suspend fun fetchAndActivate(): Boolean = remoteConfig.fetchAndActivate().tryAwait()

    actual fun getValue(key: String): RemoteConfigValue? = try {
        RemoteConfigValue(remoteConfig.getValue(key))
    } catch (throwable: Throwable) {
        null
    }
}

internal actual class RemoteConfigValue(private val remoteConfigValue: FirebaseRemoteConfigValue) {
    actual fun asBoolean(): Boolean? = try {
        remoteConfigValue.asBoolean()
    } catch (throwable: Throwable) {
        null
    }

    actual fun asString(): String? = try {
        remoteConfigValue.asString()
    } catch (throwable: Throwable) {
        null
    }

    actual fun asLong(): Long? = try {
        remoteConfigValue.asLong()
    } catch (throwable: Throwable) {
        null
    }

    actual fun asDouble(): Double? = try {
        remoteConfigValue.asDouble()
    } catch (throwable: Throwable) {
        null
    }

    actual fun asByteArray(): ByteArray? = try {
        remoteConfigValue.asByteArray()
    } catch (throwable: Throwable) {
        null
    }
}
