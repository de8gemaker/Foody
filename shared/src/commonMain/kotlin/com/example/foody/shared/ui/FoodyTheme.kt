package com.example.foody.shared.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FoodyColors = lightColorScheme(
    primary = Color(0xFF2E8B57),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFF3B33D),
    onSecondary = Color(0xFF2C1F00),
    tertiary = Color(0xFFF6EAD9),
    background = Color(0xFFF8F5EE),
    onBackground = Color(0xFF1E1E1E),
    surface = Color(0xFFFFFBF5),
    onSurface = Color(0xFF202020),
    surfaceVariant = Color(0xFFF2EBDD),
    onSurfaceVariant = Color(0xFF6A6257),
    outline = Color(0xFFD8CCB8),
)

@Composable
fun FoodyTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FoodyColors,
        typography = Typography(),
        content = content,
    )
}
