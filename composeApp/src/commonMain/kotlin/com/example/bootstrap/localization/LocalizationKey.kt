package com.example.bootstrap.localization

internal enum class LocalizationKey {
    HELLO;

    val key: String = name.lowercase()

    companion object {
        private val map = LocalizationKey.entries.associateBy(LocalizationKey::name)
        fun fromString(name: String): LocalizationKey? = map[name.uppercase()]
    }
}
