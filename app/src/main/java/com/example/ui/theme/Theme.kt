package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val YinDarkColorScheme = darkColorScheme(
    primary = TempleGold,
    onPrimary = Color(0xFF1A1300),
    primaryContainer = Color(0xFF2C2310),
    onPrimaryContainer = TempleGoldBright,
    secondary = Color(0xFF8AB4F8),
    onSecondary = Color(0xFF061833),
    secondaryContainer = Color(0xFF1F304D),
    onSecondaryContainer = Color(0xFFD6E4FF),
    tertiary = AuspiciousEmerald,
    background = CosmicNavyDark,
    onBackground = SandalwoodIvory,
    surface = CosmicSurfaceDark,
    onSurface = SandalwoodIvory,
    surfaceVariant = CosmicCardDark,
    onSurfaceVariant = Color(0xFFD5DCEB),
    outline = Color(0xFF4A5878)
)

private val YangLightColorScheme = lightColorScheme(
    primary = DeepBronze,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF9E5B4),
    onPrimaryContainer = Color(0xFF2B1A00),
    secondary = Color(0xFF24436C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9E6F9),
    onSecondaryContainer = Color(0xFF0D213F),
    tertiary = AuspiciousEmerald,
    background = SandalwoodIvory,
    onBackground = Color(0xFF1A1814),
    surface = SandalwoodCard,
    onSurface = Color(0xFF1A1814),
    surfaceVariant = SandalwoodSurface,
    onSurfaceVariant = Color(0xFF474137),
    outline = Color(0xFFC2B295)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) YinDarkColorScheme else YangLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
