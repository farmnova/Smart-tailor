package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AtelierGold,
    onPrimary = MidnightNavy,
    primaryContainer = AtelierGoldDark,
    onPrimaryContainer = AtelierGoldLight,
    secondary = StitchGold,
    onSecondary = MidnightNavy,
    secondaryContainer = DeepIndigo,
    onSecondaryContainer = WarmSilk,
    tertiary = EmeraldAccent,
    onTertiary = Color.White,
    background = MidnightNavy,
    onBackground = WarmSilk,
    surface = DeepIndigo,
    onSurface = WarmSilk,
    surfaceVariant = AtelierSurfaceDark,
    onSurfaceVariant = MutedSlate,
    outline = AtelierGold.copy(alpha = 0.4f),
    error = Color(0xFFEF4444)
)

private val LightColorScheme = lightColorScheme(
    primary = AtelierGoldDark,
    onPrimary = Color.White,
    primaryContainer = AtelierGoldLight,
    onPrimaryContainer = MidnightNavy,
    secondary = BronzeAccent,
    onSecondary = Color.White,
    secondaryContainer = SoftBone,
    onSecondaryContainer = MidnightNavy,
    tertiary = EmeraldAccent,
    onTertiary = Color.White,
    background = WarmSilk,
    onBackground = MidnightNavy,
    surface = Color.White,
    onSurface = MidnightNavy,
    surfaceVariant = SoftBone,
    onSurfaceVariant = Color(0xFF4B5563),
    outline = AtelierGoldDark.copy(alpha = 0.35f),
    error = Color(0xFFDC2626)
)

@Composable
fun SmartTailorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep atelier branding distinct
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
