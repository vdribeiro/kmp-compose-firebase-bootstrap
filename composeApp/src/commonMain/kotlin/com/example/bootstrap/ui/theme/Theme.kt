package com.example.bootstrap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
        shapes = MaterialTheme.shapes,
        typography = MaterialTheme.typography,
        content = content,
    )
}
