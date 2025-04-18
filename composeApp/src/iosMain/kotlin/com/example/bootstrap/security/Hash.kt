package com.example.bootstrap.security

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding

@OptIn(ExperimentalUnsignedTypes::class, ExperimentalForeignApi::class)
internal actual fun getHash(value: String): ByteArray = UByteArray(size = CC_SHA256_DIGEST_LENGTH).apply {
    val data = NSString.create(string = value).dataUsingEncoding(encoding = NSUTF8StringEncoding)!!.bytes
    usePinned { CC_SHA256(data = data, len = value.length.convert(), md = it.addressOf(index = 0)) }
}.toByteArray()

internal actual fun getHashString(value: String): String = getHash(value)
    .joinToString(separator = "") { it.toString(radix = 16).padStart(length = 2, padChar = '0') }
