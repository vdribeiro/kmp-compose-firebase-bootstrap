package com.example.bootstrap.file

import androidx.compose.ui.graphics.ImageBitmap

internal expect class File(path: String) {

    val path: String

    fun exists(): Boolean

    fun isDirectory(): Boolean

    fun isFile(): Boolean

    fun content(): ByteArray

    fun list(): List<String>

    fun toBitmap(): ImageBitmap?
}

internal expect fun ByteArray.toBitmap(): ImageBitmap?
