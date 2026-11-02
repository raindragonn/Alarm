package com.bluepig.alarm.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.bluepig.alarm.designsystem.R

private val Pretendard = FontFamily(
    Font(R.font.designsystem_pretendard_regular, FontWeight.Normal),
    Font(R.font.designsystem_pretendard_medium, FontWeight.Medium),
    Font(R.font.designsystem_pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.designsystem_pretendard_bold, FontWeight.Bold),
)

private fun geist(weight: FontWeight) = Font(
    R.font.designsystem_geist,
    weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private val Geist = FontFamily(
    geist(FontWeight.Thin),
    geist(FontWeight.ExtraLight),
    geist(FontWeight.Light),
    geist(FontWeight.Normal),
)

// Compose 기본(Proportional + Trim.Both)은 첫 줄 위·마지막 줄 아래를 잘라 피그마와 달라짐
private val Base = TextStyle(
    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
)

private fun pretendard(weight: FontWeight, size: Int, lineHeight: Int) = Base.copy(
    fontFamily = Pretendard, fontWeight = weight, fontSize = size.sp, lineHeight = lineHeight.sp,
)

private fun geist(weight: FontWeight, size: Int, lineHeight: Int) = Base.copy(
    fontFamily = Geist, fontWeight = weight, fontSize = size.sp, lineHeight = lineHeight.sp,
)

/** 피그마 텍스트 스타일 `v2/…`. 한글 Pretendard, 숫자 Geist */
@Immutable
data class AlarmTypography(
    val numClock: TextStyle = geist(FontWeight.Thin, 104, 112).copy(letterSpacing = (-0.02).em),
    val numDisplay: TextStyle = geist(FontWeight.ExtraLight, 72, 76),
    val numWheel: TextStyle = geist(FontWeight.Normal, 56, 64),
    val numTime: TextStyle = geist(FontWeight.Light, 40, 44),
    val numWheelDim: TextStyle = geist(FontWeight.Light, 36, 44),
    val numCaption: TextStyle = geist(FontWeight.Normal, 11, 14),
    val titleLarge: TextStyle = pretendard(FontWeight.Bold, 26, 34),
    val titleMedium: TextStyle = pretendard(FontWeight.Bold, 22, 30),
    val titleSmall: TextStyle = pretendard(FontWeight.Bold, 20, 28),
    val titleXsmall: TextStyle = pretendard(FontWeight.Bold, 17, 24),
    val topbar: TextStyle = pretendard(FontWeight.SemiBold, 17, 24),
    val wheelLabel: TextStyle = pretendard(FontWeight.SemiBold, 22, 28),
    val unit: TextStyle = pretendard(FontWeight.Medium, 18, 24),
    val bodyLarge: TextStyle = pretendard(FontWeight.Medium, 16, 22),
    val button: TextStyle = pretendard(FontWeight.SemiBold, 16, 22),
    val bodyStrong: TextStyle = pretendard(FontWeight.SemiBold, 15, 22),
    val bodyMedium: TextStyle = pretendard(FontWeight.Medium, 15, 22),
    val bodyRegular: TextStyle = pretendard(FontWeight.Normal, 15, 22),
    val labelStrong: TextStyle = pretendard(FontWeight.SemiBold, 14, 20),
    val labelMedium: TextStyle = pretendard(FontWeight.Medium, 14, 20),
    val bodySmall: TextStyle = pretendard(FontWeight.Normal, 14, 20),
    val captionStrong: TextStyle = pretendard(FontWeight.SemiBold, 13, 18),
    val captionMedium: TextStyle = pretendard(FontWeight.Medium, 13, 18),
    val captionRegular: TextStyle = pretendard(FontWeight.Normal, 13, 18),
    val smallStrong: TextStyle = pretendard(FontWeight.SemiBold, 12, 16),
    val smallRegular: TextStyle = pretendard(FontWeight.Normal, 12, 16),
)

internal val Typography = AlarmTypography()

internal val LocalAlarmTypography = staticCompositionLocalOf { Typography }
