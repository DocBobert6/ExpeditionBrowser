package com.expedition.browser.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AccentBlueLight = Color(0xFF007AFF)
val AccentBlueDark = Color(0xFF0A84FF)
val SurfaceLight = Color(0xFFF2F2F7)
val SurfaceDark = Color(0xFF1C1C1E)
val BackgroundLight = Color(0xFFFFFFFF)
val BackgroundDark = Color(0xFF000000)
val TextSecondary = Color(0xFF8E8E93)
val HairlineLight = Color(0xFFC6C6C8)
val HairlineDark = Color(0xFF38383A)
val DestructiveLight = Color(0xFFFF3B30)
val DestructiveDark = Color(0xFFFF453A)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlueLight,
    onPrimary = Color.White,
    background = BackgroundLight,
    onBackground = Color.Black,
    surface = SurfaceLight,
    onSurface = Color.Black,
    secondary = TextSecondary,
    error = DestructiveLight
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlueDark,
    onPrimary = Color.White,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    secondary = TextSecondary,
    error = DestructiveDark
)

@Composable
fun ExpeditionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
