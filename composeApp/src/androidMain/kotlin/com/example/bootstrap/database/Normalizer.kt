package com.example.bootstrap.database

import java.text.Normalizer

internal actual fun String.normalizeToAscii(): String = try {
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("[^\\p{ASCII}]".toRegex(), "")
        .lowercase()
} catch (t: Throwable) {
    this
}
