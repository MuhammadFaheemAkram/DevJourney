package com.example.devjourney.core.designsystem.theme

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

private val LightColorScheme = lightColorScheme(
    primary = JourneyIndigo,
    onPrimary = Color.White,
    secondary = JourneyTeal,
    onSecondary = Color.White,
    tertiary = JourneyAmber,
    onTertiary = Color.Black,
    error = JourneyRed,
    background = JourneyBackground,
    onBackground = JourneyOnSurface,
    surface = JourneySurface,
    onSurface = JourneyOnSurface,
    surfaceVariant = JourneySurfaceVariant,
    onSurfaceVariant = JourneyOnSurface.copy(alpha = 0.72f),
)

private val DarkColorScheme = darkColorScheme(
    primary = JourneyIndigoDark,
    onPrimary = Color(0xFF10213D),
    secondary = JourneyTealDark,
    onSecondary = Color(0xFF003B39),
    tertiary = JourneyAmberDark,
    onTertiary = Color(0xFF3D2A00),
    error = JourneyRedDark,
    background = JourneyBackgroundDark,
    onBackground = JourneyOnSurfaceDark,
    surface = JourneySurfaceDark,
    onSurface = JourneyOnSurfaceDark,
    surfaceVariant = JourneySurfaceVariantDark,
    onSurfaceVariant = JourneyOnSurfaceDark.copy(alpha = 0.76f),
)

@Composable
fun DevJourneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
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
        typography = DevJourneyTypography,
        content = content,
    )
}
