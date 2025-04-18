package com.example.bootstrap.localization

import com.example.bootstrap.firebase.Firebase
import com.example.bootstrap.firebase.config.remoteConfig

internal interface Localization {

    /**
     * Get translation string or null if it does not exist.
     */
    fun get(key: LocalizationKey): String? =
        Firebase.remoteConfig.getValue(key = key.key)?.asString()
}
