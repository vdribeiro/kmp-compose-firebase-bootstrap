package com.example.bootstrap.file

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import kotlin.math.roundToInt

internal actual class File actual constructor(actual val path: String) {

    private val file = File(path)

    actual fun exists(): Boolean = file.exists()

    actual fun isDirectory(): Boolean = file.isDirectory

    actual fun isFile(): Boolean = file.isFile

    actual fun content(): ByteArray = file.readBytes()

    actual fun list(): List<String> = file.list()?.toList().orEmpty()

    actual fun toBitmap(): ImageBitmap? = try {
        parseImageFromFile(path = path)?.asImageBitmap()
    } catch (throwable: Throwable) {
        null
    }

    /**
     * Get bitmap from file, or null if the [path] is invalid.
     */
    private fun parseImageFromFile(path: String): Bitmap? =
        BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)

            val srcWidth = outWidth
            val srcHeight = outHeight

            inJustDecodeBounds = false
            inScaled = true
            inSampleSize = calculateInSampleSize(srcWidth, srcHeight)
            inDensity = if (srcWidth > srcHeight) srcWidth else srcHeight

            val scaledDimensions = calculateScaledSizes(srcWidth, srcHeight)
            inTargetDensity = if (scaledDimensions.x > scaledDimensions.y) scaledDimensions.x * inSampleSize else scaledDimensions.y * inSampleSize
        })

    /**
     * Calculate the largest 'inSampleSize' value that is a power of 2 and keeps both [width] and [height] larger
     * than the defined maximum image [MAX_SIZE] in pixels.
     * The sample size is the number of pixels in either dimension that correspond to a single pixel in the decoded bitmap.
     */
    private fun calculateInSampleSize(width: Int, height: Int): Int {
        val srcWidth = if (width > 0) width else MAX_SIZE
        val srcHeight = if (height > 0) height else MAX_SIZE

        var inSampleSize = 1
        while ((srcWidth / 2) / inSampleSize >= MAX_SIZE && (srcHeight / 2) / inSampleSize >= MAX_SIZE) inSampleSize *= 2
        return inSampleSize
    }

    /**
     * Calculate the scaled sizes for the given [width] and [height] in pixels.
     */
    private fun calculateScaledSizes(width: Int, height: Int): Point {
        val srcWidth = if (width > 0) width else MAX_SIZE
        val srcHeight = if (height > 0) height else MAX_SIZE

        if (srcWidth <= MAX_SIZE && srcHeight <= MAX_SIZE) return Point(srcWidth, srcHeight)

        val scale = if (srcWidth > srcHeight) (MAX_SIZE / srcWidth.toFloat()) else (MAX_SIZE / srcHeight.toFloat())
        val dstWidth = (srcWidth * scale).roundToInt()
        val dstHeight = (srcHeight * scale).roundToInt()

        return if (dstWidth == 0 || dstHeight == 0) Point(srcWidth, srcHeight) else Point(dstWidth, dstHeight)
    }

    companion object {
        // Maximum image size as 1080p
        private const val MAX_SIZE = 1920
    }
}

internal actual fun ByteArray.toBitmap(): ImageBitmap? = try {
    BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
} catch (throwable: Throwable) {
    null
}
