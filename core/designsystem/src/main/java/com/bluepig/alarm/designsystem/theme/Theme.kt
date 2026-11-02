package com.bluepig.alarm.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun AlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    // Material3 컴포넌트(바텀시트·다이얼로그·리플)의 기본색만 맞춤. 화면은 AlarmTheme.colors를 씀
    val scheme = (if (darkTheme) darkColorScheme() else lightColorScheme()).copy(
        primary = colors.accent,
        onPrimary = colors.onAccent,
        background = colors.background,
        onBackground = colors.text,
        surface = colors.surface,
        onSurface = colors.text,
        surfaceContainerLow = colors.surface,
        onSurfaceVariant = colors.sub,
        outline = colors.dim,
        error = colors.danger,
        scrim = colors.text,
    )
    CompositionLocalProvider(
        LocalAlarmColors provides colors,
        LocalAlarmTypography provides Typography,
    ) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

object AlarmTheme {
    val colors: AlarmColors
        @Composable @ReadOnlyComposable get() = LocalAlarmColors.current

    val typography: AlarmTypography
        @Composable @ReadOnlyComposable get() = LocalAlarmTypography.current
}
