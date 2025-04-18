package com.example.bootstrap.ui.components.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.example.bootstrap.file.File
import kotlin.math.roundToInt

@Composable
internal fun imagePainter(file: File): Painter = object: Painter() {
    private var image: ImageBitmap? by remember { mutableStateOf(value = null) }

    init {
        image = file.toBitmap()
    }

    override val intrinsicSize: Size
        get() = IntSize(image?.width ?: 0, image?.height ?: 0).toSize()

    override fun DrawScope.onDraw() {
        drawImage(
            image = image ?: return,
            dstSize = IntSize(
                width = this@onDraw.size.width.roundToInt(),
                height = this@onDraw.size.height.roundToInt()
            )
        )
    }
}
