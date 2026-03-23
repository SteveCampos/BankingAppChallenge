package com.stevecampos.core.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BankingPrimary,
    onPrimary = BankingOnPrimary,
    primaryContainer = BankingPrimaryContainer,
    onPrimaryContainer = BankingOnPrimaryContainer,
    secondary = BankingSecondary,
    onSecondary = BankingOnSecondary,
    secondaryContainer = BankingSecondaryContainer,
    onSecondaryContainer = BankingOnSecondaryContainer,
    tertiary = BankingTertiary,
    onTertiary = BankingOnTertiary,
    tertiaryContainer = BankingTertiaryContainer,
    onTertiaryContainer = BankingOnTertiaryContainer,
    background = BankingBackground,
    onBackground = BankingOnBackground,
    surface = BankingSurface,
    onSurface = BankingOnSurface,
    surfaceVariant = BankingSurfaceVariant,
    onSurfaceVariant = BankingOnSurfaceVariant,
    surfaceTint = BankingPrimary,
    outline = BankingOutline,
    surfaceContainer = BankingSurfaceContainer,
    surfaceContainerLow = BankingSurfaceContainerLow,
    surfaceContainerHigh = BankingSurfaceContainerHigh,
    error = BankingError,
)

private val DarkColorScheme = darkColorScheme(
    primary = BankingPrimaryDark,
    onPrimary = BankingOnPrimaryDark,
    primaryContainer = BankingPrimaryContainerDark,
    onPrimaryContainer = BankingOnPrimaryContainerDark,
    secondary = BankingSecondaryDark,
    onSecondary = BankingOnSecondaryDark,
    secondaryContainer = BankingSecondaryContainerDark,
    onSecondaryContainer = BankingOnSecondaryContainerDark,
    tertiary = BankingTertiaryDark,
    onTertiary = BankingOnTertiaryDark,
    tertiaryContainer = BankingTertiaryContainerDark,
    onTertiaryContainer = BankingOnTertiaryContainerDark,
    background = BankingBackgroundDark,
    onBackground = BankingOnBackgroundDark,
    surface = BankingSurfaceDark,
    onSurface = BankingOnSurfaceDark,
    surfaceVariant = BankingSurfaceVariantDark,
    onSurfaceVariant = BankingOnSurfaceVariantDark,
    surfaceTint = BankingPrimaryDark,
    outline = BankingOutlineDark,
    surfaceContainer = BankingSurfaceContainerDark,
    surfaceContainerLow = BankingSurfaceContainerLowDark,
    surfaceContainerHigh = BankingSurfaceContainerHighDark,
    error = BankingError,
)

@Composable
fun BankingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BankingTypography,
        content = content,
    )
}
