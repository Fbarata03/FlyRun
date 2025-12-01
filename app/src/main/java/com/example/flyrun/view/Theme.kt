package com.example.flyrun.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Black = Color(0xFF000000)
private val F1Red = Color(0xFFC80000)
private val MotoBlue = Color(0xFF0055A4)
private val LightBg = Color(0xFFFFFFFF)
private val DarkBg = Color(0xFF121212)

private val DarkColorScheme = darkColorScheme(
    primary = F1Red,
    secondary = MotoBlue,
    tertiary = Color(0xFF1976D2),
    background = DarkBg,
    surface = DarkBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = F1Red,
    secondary = MotoBlue,
    tertiary = Color(0xFF1976D2),
    background = LightBg,
    surface = LightBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Black,
    onSurface = Black
)

private val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
)

@Composable
fun FlyRunTheme(dark: Boolean = true, content: @Composable () -> Unit) {
    val colors = if (dark) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
}

