package com.github.se.gomeet.ui.theme

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
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme =
    darkColorScheme(
        primary = Color.Black,
        secondary = DarkGrey,
        tertiary = White,
        background = Color.Black,
        outline = DarkCyan,
        primaryContainer = DarkModeBackground,
        secondaryContainer = DarkModeBackground,
        tertiaryContainer = Color(0xFFEEEEEE),
        outlineVariant = DodgerBlue,
        onBackground = Color.White,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = White,
        secondary = DarkGrey,
        tertiary = DarkGrey,
        background = White,
        primaryContainer = AliceBlue,
        secondaryContainer = LightGray,
        tertiaryContainer = Color(0xFFEEEEEE),
        outline = Cyan,
        outlineVariant = DodgerBlue,
        onBackground = Color.Black)

@Composable
fun GoMeetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
  val colorScheme =
      when {
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

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun SetStatusBarColor(color: Color) {
  val systemUiController = rememberSystemUiController()
  SideEffect { systemUiController.setSystemBarsColor(color) }
}
