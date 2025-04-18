package com.example.bootstrap.security

import java.security.SecureRandom
import java.util.UUID

/**
 * Get a universally unique identifier (UUID) using the best available algorithm to prevent collisions. First it tries to generate a UUID type 4.
 * If it fails, then it tries a UUID type 3.
 */
internal actual fun generateUuid(): String = try {
    // UUID type 4
    UUID.randomUUID().toString()
} catch (throwable: Throwable) {
    // UUID type 3
    val byteArray = ByteArray(16).apply { SecureRandom().nextBytes(this) }
    UUID.nameUUIDFromBytes(byteArray).toString()
}
