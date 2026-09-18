package com.example.shitolpata.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LeafPrimary,
    onPrimary = NightBg0,
    primaryContainer = LeafPrimaryDark,
    onPrimaryContainer = NightTextPrimary,
    secondary = SunlightSecondary,
    onSecondary = NightBg0,
    background = NightBg0,
    onBackground = NightTextPrimary,
    surface = NightBg1,
    onSurface = NightTextPrimary,
    surfaceVariant = NightBg2,
    onSurfaceVariant = NightTextSecondary,
    outline = NightBorder
)

private val LightColorScheme = lightColorScheme(
    primary = LeafPrimary,
    onPrimary = PaperBg0,
    primaryContainer = LeafPrimaryLight,
    onPrimaryContainer = PaperTextPrimary,
    secondary = SunlightSecondary,
    onSecondary = PaperBg0,
    background = PaperBg0,
    onBackground = PaperTextPrimary,
    surface = PaperBg1,
    onSurface = PaperTextPrimary,
    surfaceVariant = PaperBg2,
    onSurfaceVariant = PaperTextSecondary,
    outline = PaperBorder
)

@Composable
fun ShitolPataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
