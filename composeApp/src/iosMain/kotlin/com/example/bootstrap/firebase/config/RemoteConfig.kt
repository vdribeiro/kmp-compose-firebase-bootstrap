package com.example.bootstrap.firebase.config

import cocoapods.FirebaseRemoteConfig.FIRRemoteConfig
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigFetchAndActivateStatus
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigSettings
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigSource
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigValue
import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.toByteArray
import com.example.bootstrap.tryAwaitWithResult

internal actual val Firebase.remoteConfig: RemoteConfig get() = com.example.bootstrap.firebase.config.RemoteConfig(remoteConfig = FIRRemoteConfig.remoteConfig())

internal actual class RemoteConfig(private val remoteConfig: FIRRemoteConfig) {
    actual val all: Map<String, RemoteConfigValue>
        get() = listOf(
            FIRRemoteConfigSource.FIRRemoteConfigSourceDefault,
            FIRRemoteConfigSource.FIRRemoteConfigSourceStatic,
            FIRRemoteConfigSource.FIRRemoteConfigSourceRemote,
        ).map { source ->
            remoteConfig.allKeysFromSource(source = source).mapNotNull {
                val key = it as? String ?: return@mapNotNull null
                key to com.example.bootstrap.firebase.config.RemoteConfigValue(
                    remoteConfigValue = remoteConfig.configValueForKey(
                        key = key,
                        source = source
                    )
                )
            }
        }.flatten().toMap()

    actual suspend fun settings(settings: RemoteConfigSettings): RemoteConfig = apply {
        remoteConfig.setConfigSettings(configSettings = FIRRemoteConfigSettings().apply {
            minimumFetchInterval = settings.minimumFetchIntervalInSeconds.toDouble()
            fetchTimeout = settings.fetchTimeoutInSeconds.toDouble()
        })
    }

    actual suspend fun setDefaults(defaults: Map<String, Any?>): RemoteConfig = apply {
        remoteConfig.setDefaults(defaults = defaults.toMap())
    }

    actual suspend fun fetchAndActivate(): Boolean {
        return remoteConfig.tryAwaitWithResult { fetchAndActivateWithCompletionHandler(completionHandler = it) } ==
                FIRRemoteConfigFetchAndActivateStatus.FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote
    }

    actual fun getValue(key: String): RemoteConfigValue? = try {
        com.example.bootstrap.firebase.config.RemoteConfigValue(remoteConfigValue = remoteConfig.configValueForKey(key = key))
    } catch (throwable: Throwable) {
        null
    }
}

internal actual class RemoteConfigValue(private val remoteConfigValue: FIRRemoteConfigValue) {
    actual fun asBoolean(): Boolean? = try {
        remoteConfigValue.boolValue
    } catch (throwable: Throwable) {
        null
    }

    actual fun asString(): String? = try {
        remoteConfigValue.stringValue
    } catch (throwable: Throwable) {
        null
    }

    actual fun asLong(): Long? = try {
        remoteConfigValue.numberValue.longValue
    } catch (throwable: Throwable) {
        null
    }

    actual fun asDouble(): Double? = try {
        remoteConfigValue.numberValue.doubleValue
    } catch (throwable: Throwable) {
        null
    }

    actual fun asByteArray(): ByteArray? = try {
        remoteConfigValue.dataValue.toByteArray()
    } catch (throwable: Throwable) {
        null
    }
}
