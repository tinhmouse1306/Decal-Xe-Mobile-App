package com.example.decalxeandroid.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DecalXeBlueLight,
    onPrimary = Color.White,
    primaryContainer = DecalXeBlueDark,
    onPrimaryContainer = Color.White,
    secondary = DecalXeOrange,
    onSecondary = Color.White,
    secondaryContainer = DecalXeOrangeDark,
    onSecondaryContainer = Color.White,
    tertiary = DecalXeGrayLight,
    onTertiary = Color.White,
    background = DecalXeGrayDark,
    onBackground = Color.White,
    surface = DecalXeGray,
    onSurface = Color.White,
    surfaceVariant = DecalXeGrayLight,
    onSurfaceVariant = Color.White,
    error = DecalXeRed,
    onError = Color.White,
    errorContainer = DecalXeRedLight,
    onErrorContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DecalXeBlue,
    onPrimary = Color.White,
    primaryContainer = DecalXeBlueLight,
    onPrimaryContainer = DecalXeBlueDark,
    secondary = DecalXeOrange,
    onSecondary = Color.White,
    secondaryContainer = DecalXeOrangeLight,
    onSecondaryContainer = DecalXeOrangeDark,
    tertiary = DecalXeGray,
    onTertiary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = DecalXeGrayDark,
    surface = Color.White,
    onSurface = DecalXeGrayDark,
    surfaceVariant = Color(0xFFF3F3F3),
    onSurfaceVariant = DecalXeGray,
    error = DecalXeRed,
    onError = Color.White,
    errorContainer = DecalXeRedLight,
    onErrorContainer = DecalXeRed
)

@Composable
fun DecalXeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}