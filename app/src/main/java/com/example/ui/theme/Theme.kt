package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light ("Morning temple"):
// background #FFF7EC (cream), surface #FFFFFF, surfaceVariant #F7E9D7, primary #5B0E1E (maroon),
// onPrimary #FFFFFF, secondary #F28C28 (saffron), tertiary #D4A537 (gold),
// onBackground/onSurface #2B1A12, outline #E3CDB0.
val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF6B3300),
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = Color(0xFFFFF3D0),
    onTertiaryContainer = Color(0xFF5A4300),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = LightInauspiciousRed,
    onError = Color.White,
    errorContainer = LightInauspiciousRedBg,
    onErrorContainer = LightInauspiciousRed
)

// DARK ("Temple at dusk"):
// background #1A0A0F, surface #2A1218, surfaceVariant #3A1A22, primary #F28C28,
// onPrimary #1A0A0F, secondary #D4A537, tertiary #5B0E1E,
// onBackground/onSurface #FBEFE3, outline #5A2E36.
val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkSecondary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = Color(0xFF4A2A14),
    onSecondaryContainer = DarkSecondary,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = Color(0xFF38121A),
    onTertiaryContainer = DarkOnSurface,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = DarkInauspiciousRed,
    onError = DarkBackground,
    errorContainer = DarkInauspiciousRedBg,
    onErrorContainer = DarkInauspiciousRed
)

/** Muhurtham & Panchang status colors tailored for contrast in both themes. */
data class MuhurthamColors(
    val auspiciousGreen: Color,
    val auspiciousGreenBg: Color,
    val inauspiciousRed: Color,
    val inauspiciousRedBg: Color
)

val LocalMuhurthamColors = staticCompositionLocalOf {
    MuhurthamColors(
        auspiciousGreen = LightAuspiciousGreen,
        auspiciousGreenBg = LightAuspiciousGreenBg,
        inauspiciousRed = LightInauspiciousRed,
        inauspiciousRedBg = LightInauspiciousRedBg
    )
}

val MaterialTheme.muhurthamColors: MuhurthamColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMuhurthamColors.current

@Composable
fun ShubhodayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic color explicitly disabled to preserve authentic brand temple colors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val muhurthamColors = if (darkTheme) {
        MuhurthamColors(
            auspiciousGreen = DarkAuspiciousGreen,
            auspiciousGreenBg = DarkAuspiciousGreenBg,
            inauspiciousRed = DarkInauspiciousRed,
            inauspiciousRedBg = DarkInauspiciousRedBg
        )
    } else {
        MuhurthamColors(
            auspiciousGreen = LightAuspiciousGreen,
            auspiciousGreenBg = LightAuspiciousGreenBg,
            inauspiciousRed = LightInauspiciousRed,
            inauspiciousRedBg = LightInauspiciousRedBg
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                // Light status bars when light theme is active (dark icons), dark when dark theme
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalMuhurthamColors provides muhurthamColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
