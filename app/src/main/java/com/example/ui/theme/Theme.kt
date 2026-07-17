package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = IndigoAccent,
    secondary = PurpleAccent,
    tertiary = GlowGreen,
    background = PureBlack,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = SolidWhite,
    onSecondary = SolidWhite,
    onBackground = SolidWhite,
    onSurface = SolidWhite,
    onSurfaceVariant = TextGray
  )

private val LightColorScheme = DarkColorScheme // Force dark theme for the AppForge look!

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force darkTheme
  dynamicColor: Boolean = false, // Force false so our customized palette shines!
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
