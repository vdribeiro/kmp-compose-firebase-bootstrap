package com.example.bootstrap.security

import java.security.MessageDigest

internal actual fun getHash(value: String): ByteArray = MessageDigest.getInstance("SHA-256")
    .digest(value.toByteArray())

internal actual fun getHashString(value: String): String = getHash(value)
    .fold("") { str, it -> str + "%02x".format(it) }
