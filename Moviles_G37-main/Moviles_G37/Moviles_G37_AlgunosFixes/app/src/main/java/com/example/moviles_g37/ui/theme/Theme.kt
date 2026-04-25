package com.example.moviles_g37.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SenecaYellow,
    secondary = DimGrey,
    tertiary = Graphite,
    background = Onyx,
    surface = Graphite,
    onPrimary = SenecaTextDark,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = SenecaYellow,
    secondary = DimGrey,
    tertiary = Graphite,
    background = White,
    surface = White,
    onPrimary = SenecaTextDark,
    onSecondary = White,
    onTertiary = White,
    onBackground = SenecaTextDark,
    onSurface = SenecaTextDark
)

@Composable
fun Moviles_G37Theme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}