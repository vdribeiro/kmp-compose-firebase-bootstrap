package com.example.bootstrap.file

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.bootstrap.toByteArray
import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.jetbrains.skia.Image
import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.fileHandleForReadingAtPath
import platform.Foundation.readDataToEndOfFile

@OptIn(ExperimentalForeignApi::class)
internal actual class File actual constructor(actual val path: String) {

    private val fileManager = NSFileManager.defaultManager

    actual fun exists(): Boolean = fileManager.fileExistsAtPath(path = path)

    actual fun isDirectory(): Boolean = memScoped {
        val isDirectory = alloc<BooleanVar>()
        val exists = fileManager.fileExistsAtPath(path = path, isDirectory = isDirectory.ptr)
        exists && isDirectory.value
    }

    actual fun isFile(): Boolean = memScoped {
        val isDirectory = alloc<BooleanVar>()
        val exists = fileManager.fileExistsAtPath(path = path, isDirectory = isDirectory.ptr)
        exists && !isDirectory.value
    }

    actual fun content(): ByteArray = fileManager.contentsAtPath(path = path)?.toByteArray()!!

    actual fun list(): List<String> = fileManager.directoryContentsAtPath(path = path)?.map { it.toString() }!!

    actual fun toBitmap(): ImageBitmap? = try {
        getFileBytes(path = path)?.toBitmap() ?: throw Throwable(message = "Invalid path")
    } catch (throwable: Throwable) {
        null
    }

    private fun getFileBytes(path: String): ByteArray? {
        return NSFileHandle.fileHandleForReadingAtPath(
            path = NSURL.URLWithString(URLString = path)?.relativePath ?: return null
        )?.readDataToEndOfFile()?.toByteArray()
    }
}

internal actual fun ByteArray.toBitmap(): ImageBitmap? = try {
    Image.makeFromEncoded(bytes = this).toComposeImageBitmap()
} catch (throwable: Throwable) {
    null
}
