package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = TempleGold,
    onPrimary = TempleMaroonDark,
    primaryContainer = TempleMaroon,
    onPrimaryContainer = TempleGoldLight,
    secondary = TempleSaffronLight,
    onSecondary = Color.Black,
    secondaryContainer = TempleSaffronDark,
    tertiary = TempleGoldLight,
    onTertiary = Color.Black,
    background = TempleDarkBg,
    onBackground = TempleTextPrimaryDark,
    surface = TempleDarkSurface,
    onSurface = TempleTextPrimaryDark,
    surfaceVariant = TempleDarkCardElevated,
    onSurfaceVariant = TempleTextSecondaryDark,
    outline = TempleBorderDark
)

val LightColorScheme = lightColorScheme(
    primary = TempleMaroon,
    onPrimary = Color.White,
    primaryContainer = TempleMaroonLight,
    onPrimaryContainer = TempleWarmCream,
    secondary = TempleSaffron,
    onSecondary = Color.White,
    secondaryContainer = TempleSaffronLight,
    tertiary = TempleGold,
    onTertiary = TempleMaroonDark,
    background = TempleWarmCream,
    onBackground = TempleTextPrimaryLight,
    surface = TempleWarmCreamSurface,
    onSurface = TempleTextPrimaryLight,
    surfaceVariant = Color(0xFFF5E6D0),
    onSurfaceVariant = TempleTextSecondaryLight,
    outline = TempleBorderLight
)

@Composable
fun ShubhodayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
