package com.example.bootstrap.database

import platform.Foundation.NSLocale
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.currentLocale
import platform.Foundation.stringByFoldingWithOptions

internal actual fun String.normalizeToAscii(): String = try {
    NSString.create(string = this)
        .stringByFoldingWithOptions(128u, NSLocale.currentLocale())
        .lowercase()
} catch (t: Throwable) {
    this
}
