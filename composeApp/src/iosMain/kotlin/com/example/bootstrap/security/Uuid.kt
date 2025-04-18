package com.example.bootstrap.security

import platform.Foundation.NSUUID

/**
 * Get a universally unique identifier (UUID) using the best available algorithm to prevent collisions.
 */
internal actual fun generateUuid(): String = NSUUID().UUIDString().lowercase()
