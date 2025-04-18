package com.example.bootstrap.security

internal expect fun getHash(value: String): ByteArray

internal expect fun getHashString(value: String): String
