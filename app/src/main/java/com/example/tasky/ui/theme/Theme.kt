package com.example.tasky.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun TaskyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

val headerStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    fontSize = 16.sp,
    color = Color.White
)

val detailTypeStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    fontSize = 14.sp,
    color = Color.Gray
)

val detailTitleStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Start,
    fontSize = 26.sp,
    color = Color.Black
)

val detailDescriptionStyle = TextStyle(
    textAlign = TextAlign.Start,
    lineHeight = 20.sp,
    fontSize = 14.sp,
    color = Color.Black
)

val greenSaveButtonStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    fontSize = 16.sp,
    color = TaskyGreen,
)