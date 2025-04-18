package com.example.bootstrap.toggle

internal enum class ToggleKey {
    SCHEDULER;

    val key: String = name.lowercase()

    companion object {
        private val map = ToggleKey.entries.associateBy(ToggleKey::name)
        fun fromString(name: String): ToggleKey? = map[name.uppercase()]
    }
}
