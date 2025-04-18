package com.example.bootstrap.ui.components.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.bootstrap.file.File
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun Avatar(avatarContent: AvatarContent) = with(avatarContent) {
    Image(
        modifier = Modifier
            .size(size = imageSize.dp)
            .clip(shape = shape),
        contentScale = scale,
        painter = when (photo) {
            null -> painterResource(Res.drawable.ic_launcher_foreground)
            else -> imagePainter(file = photo)
        },
        contentDescription = name
    )
}

internal data class AvatarContent(
    val name: String,
    val photo: File?,
    val imageSize: Float,
    val scale: ContentScale,
    val shape: Shape
)
