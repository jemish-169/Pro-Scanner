package com.app.scanner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.app.scanner.util.ThemeOption

private val DarkColorScheme = darkColorScheme(
    primary = LightBlue,
    onPrimary = Black,
    primaryContainer = DarkBlue,
    onPrimaryContainer = White,
    secondary = DarkGrey,
    onSecondary = White,
    secondaryContainer = DarkGrey,
    onSecondaryContainer = Black,
    background = Black,
    onBackground = White,
    surface = Black,
    onSurface = White,
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = Black,
    secondary = LightGrey,
    onSecondary = Black,
    secondaryContainer = DarkGrey,
    onSecondaryContainer = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
)

@Composable
fun AppTheme(
    themeOption: ThemeOption,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeOption) {
        ThemeOption.LIGHT -> LightColorScheme
        ThemeOption.DARK -> DarkColorScheme
        ThemeOption.SYSTEM -> {
            if (darkTheme) DarkColorScheme
            else LightColorScheme
        }

        ThemeOption.DYNAMIC -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else if (darkTheme) DarkColorScheme
            else LightColorScheme
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                if (themeOption == ThemeOption.DYNAMIC) {
                    if (colorScheme.primary.luminance() < 0.25f) false else true
                } else {
                    (darkTheme || themeOption == ThemeOption.DARK)
                }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
