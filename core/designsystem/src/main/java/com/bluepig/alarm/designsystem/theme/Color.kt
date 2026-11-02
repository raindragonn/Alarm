package com.bluepig.alarm.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** 피그마 변수 컬렉션 `Color v2` (Light/Dark) */
@Immutable
data class AlarmColors(
    val background: Color,
    val surface: Color,
    val fill: Color,
    val band: Color,
    val text: Color,
    val sub: Color,
    val dim: Color,
    val line: Color,
    val accent: Color,
    val onAccent: Color,
    val danger: Color,
    val thumbOff: Color,
    val knob: Color,
    val bar: Color,
    val ringBg: Color,
    val overlay: Color,
    val isDark: Boolean,
)

internal val LightColors = AlarmColors(
    background = Color(0xFFF3F4F0),
    surface = Color(0xFFFFFFFF),
    fill = Color(0xFFECEDE9),
    band = Color(0xFFFFFFFF),
    text = Color(0xFF141518),
    sub = Color(0xFF686D75),
    dim = Color(0xFFC3C6CC),
    line = Color(0xFFE4E5E1),
    accent = Color(0xFF2F54EB),
    onAccent = Color(0xFFFFFFFF),
    danger = Color(0xFFD93025),
    thumbOff = Color(0xFFFFFFFF),
    knob = Color(0xFFFFFFFF),
    bar = Color(0xFFFFFFFF),
    ringBg = Color(0xFFFFFFFF),
    overlay = Color(0x0F141518),
    isDark = false,
)

internal val DarkColors = AlarmColors(
    background = Color(0xFF0B0C0E),
    surface = Color(0xFF15171B),
    fill = Color(0xFF1F2227),
    band = Color(0xFF1F2227),
    text = Color(0xFFF2F3F5),
    sub = Color(0xFF8A8F98),
    dim = Color(0xFF3A3E45),
    line = Color(0xFF25282D),
    accent = Color(0xFF8FA8FF),
    onAccent = Color(0xFF0B0C0E),
    danger = Color(0xFFFF6B6B),
    thumbOff = Color(0xFF8A8F98),
    knob = Color(0xFFF2F3F5),
    bar = Color(0xFF0B0C0E),
    ringBg = Color(0xFF000000),
    overlay = Color(0x14F2F3F5),
    isDark = true,
)

internal val LocalAlarmColors = staticCompositionLocalOf { LightColors }
