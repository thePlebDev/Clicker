package com.example.clicker.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary
)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
)
private val typography = Typography(
    headlineSmall = TextStyle(
        fontSize = 15.sp,
    ),
    headlineMedium = TextStyle(
        fontSize = 20.sp,
    ),
    headlineLarge = TextStyle(
        fontSize = 25.sp,
    ),
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
//    val colors = if (!useDarkTheme) {
//        LightColors
//    } else {
//        DarkColors
//    }
//
    val colors = when{
        (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) -> DarkColors
        (!useDarkTheme) -> LightColors
        else -> DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}