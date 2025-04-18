package com.example.bootstrap.logger

import android.util.Log

internal actual object Logger {

    actual fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    actual fun error(tag: String, message: String) {
        Log.e(tag, message)
    }
}
