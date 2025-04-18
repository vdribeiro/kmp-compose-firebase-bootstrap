package com.example.bootstrap.logger

internal expect object Logger {

    fun debug(tag: String = "DEBUG", message: String)

    fun info(tag: String = "INFO", message: String)

    fun error(tag: String = "ERROR", message: String)
}
