package com.example.shitolpata.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LeafPrimary,
    onPrimary = Color.White,
    primaryContainer = LeafPrimaryDark,
    onPrimaryContainer = DarkTextPrimary,
    secondary = SunlightSecondary,
    onSecondary = Color.Black,
    background = DarkPageBg,
    onBackground = DarkTextPrimary,
    surface = DarkCardBg,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkTertiaryBg,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderDefault,
    outlineVariant = DarkBorderLight
)

private val LightColorScheme = lightColorScheme(
    primary = LeafPrimary,
    onPrimary = Color.White,
    primaryContainer = LeafPrimaryLight,
    onPrimaryContainer = PaperTextPrimary,
    secondary = SunlightSecondary,
    onSecondary = Color.Black,
    background = PaperBg0,
    onBackground = PaperTextPrimary,
    surface = PaperBg1,
    onSurface = PaperTextPrimary,
    surfaceVariant = PaperBg2,
    onSurfaceVariant = PaperTextSecondary,
    outline = PaperBorder,
    outlineVariant = PaperBorder
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
