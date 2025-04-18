package com.example.bootstrap.logger

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEFAULT
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal

@OptIn(ExperimentalForeignApi::class)
internal actual object Logger {

    actual fun debug(tag: String, message: String) {
        _os_log_internal(dso = __dso_handle.ptr, log = OS_LOG_DEFAULT, type = OS_LOG_TYPE_INFO, message = message)
    }

    actual fun info(tag: String, message: String) {
        _os_log_internal(dso = __dso_handle.ptr, log = OS_LOG_DEFAULT, type = OS_LOG_TYPE_DEFAULT, message = message)
    }

    actual fun error(tag: String, message: String) {
        _os_log_internal(dso = __dso_handle.ptr, log = OS_LOG_DEFAULT, type = OS_LOG_TYPE_FAULT, message = message)
    }
}
