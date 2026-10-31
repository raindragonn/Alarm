package com.bluepig.alarm.feature.alarm

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.ButtonStyle
import com.bluepig.alarm.designsystem.component.MediaTile
import com.bluepig.alarm.designsystem.component.MediaTileType
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import java.text.SimpleDateFormat
import java.util.Date
import android.text.format.DateFormat as AndroidDateFormat
import com.bluepig.alarm.designsystem.R as DesignSystemR

/**
 * 알람 울림·미리보기 화면 (제품 0004). 소리·진동은 울림 서비스가 냄 (기술 0026)
 * @param canPlayVideo 재부팅 후 잠금 해제 전에는 WebView를 못 써서 false (C7). 그땐 타일 + 기본 알람음
 * @param banner 밀어서 끄기 아래 배너 (제품 0006)
 */
@Composable
fun AlarmRingRoute(
    canPlayVideo: Boolean,
    onDismiss: () -> Unit,
    onVideoPlaying: () -> Unit,
    onVideoFailed: () -> Unit,
    onNotFound: () -> Unit,
    banner: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlarmRingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        AlarmRingUiState.Loading -> Box(modifier.fillMaxSize().background(AlarmTheme.colors.ringBg))
        AlarmRingUiState.NotFound -> LaunchedEffect(Unit) { onNotFound() }
        is AlarmRingUiState.Ringing -> {
            // 울리는 중에는 뒤로 가기로 끄지 않음, 미리보기는 종료
            BackHandler { if (state.isPreview) onDismiss() }
            AlarmRingScreen(state, canPlayVideo, onDismiss, onVideoPlaying, onVideoFailed, banner, modifier)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AlarmRingScreen(
    state: AlarmRingUiState.Ringing,
    canPlayVideo: Boolean,
    onDismiss: () -> Unit,
    onVideoPlaying: () -> Unit,
    onVideoFailed: () -> Unit,
    banner: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AlarmTheme.colors
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(colors.ringBg)
            .ringGlow(colors.accent)
            .semantics { testTagsAsResourceId = true },
    ) {
        val showVideo = canPlayVideo && state.alarm.media is TubeMedia
        val media = @Composable { MediaSection(state.alarm, showVideo, onVideoPlaying, onVideoFailed) }
        val action = @Composable {
            if (state.isPreview) {
                AlarmButton(
                    stringResource(R.string.alarm_preview_end),
                    onDismiss,
                    Modifier.widthIn(max = 312.dp).fillMaxWidth(),
                    style = ButtonStyle.Secondary,
                )
            } else {
                SlideToStop(stringResource(R.string.alarm_slide_to_stop), onDismiss, Modifier.widthIn(max = 312.dp))
            }
        }
        // 가로로 낮은 화면(폰 가로·태블릿 가로)은 두 칸 (U5)
        if (maxWidth > maxHeight && maxHeight < 560.dp) {
            Row(
                Modifier.fillMaxSize().systemBarsPadding().padding(Space.s24),
                horizontalArrangement = Arrangement.spacedBy(Space.s32),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Clock(state)
                    Spacer(Modifier.height(Space.s16))
                    Details(state.alarm)
                }
                // 미디어는 버튼·배너를 뺀 남은 높이를 씀
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.weight(1f, fill = false)) { media() }
                    action()
                    Spacer(Modifier.height(Space.s16))
                    banner(Modifier.fillMaxWidth())
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = Space.s24),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Clock(state, Modifier.padding(top = if (state.isPreview) Space.s20 else Space.s40))
                media()
                Details(state.alarm)
                Spacer(Modifier.weight(1f))
                action()
                Spacer(Modifier.height(Space.s16))
                banner(Modifier.fillMaxWidth())
                Spacer(Modifier.height(Space.s16))
            }
        }
    }
}

@Composable
private fun Clock(state: AlarmRingUiState.Ringing, modifier: Modifier = Modifier) {
    val colors = AlarmTheme.colors
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val (date, clock) = remember(state.minuteMillis, locale) {
        val now = Date(state.minuteMillis)
        val datePattern = AndroidDateFormat.getBestDateTimePattern(locale, "MMMMdEEEE")
        val clockPattern = if (AndroidDateFormat.is24HourFormat(context)) "HH:mm" else "hh:mm"
        SimpleDateFormat(datePattern, locale).format(now) to SimpleDateFormat(clockPattern, locale).format(now)
    }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (state.isPreview) {
            Text(
                stringResource(R.string.alarm_preview_badge),
                Modifier
                    .background(colors.overlay, RoundedCornerShape(Radius.sm))
                    .padding(horizontal = Space.s12, vertical = Space.s4),
                style = AlarmTheme.typography.smallStrong,
                color = colors.sub,
            )
            Spacer(Modifier.height(Space.s8))
        }
        Text(date, style = AlarmTheme.typography.bodyLarge, color = colors.sub)
        Text(clock, Modifier.testTag("tv_time"), style = AlarmTheme.typography.numClock, color = colors.text, maxLines = 1)
    }
}

@Composable
private fun MediaSection(alarm: Alarm, showVideo: Boolean, onVideoPlaying: () -> Unit, onVideoFailed: () -> Unit) {
    val media = alarm.media
    if (showVideo && media is TubeMedia) {
        YoutubeVideo(
            media.videoId,
            onVideoPlaying,
            onVideoFailed,
            // 가로 화면에서는 남은 높이에 맞춰 줄어듦 (세로는 너비를 채움)
            Modifier.padding(top = Space.s20, bottom = Space.s16).aspectRatio(16f / 9f, matchHeightConstraintsFirst = true),
        )
        return
    }
    val (type, thumbnail) = when (media) {
        is MusicMedia -> MediaTileType.Song to media.thumbnail
        is RingtoneMedia -> MediaTileType.Ringtone to null
        is TubeMedia -> MediaTileType.YouTube to media.thumbnail
    }
    BoxWithConstraints(Modifier.padding(top = Space.s20, bottom = Space.s16)) {
        // 가로 화면은 시계 아래 남은 높이에 맞춤
        val size = minOf(184.dp, maxHeight)
        MediaTile(
            type,
            Modifier.border(1.dp, AlarmTheme.colors.line, RoundedCornerShape(size * TILE_RADIUS_RATIO)),
            thumbnail = thumbnail,
            size = size,
            cornerRadius = size * TILE_RADIUS_RATIO,
        )
    }
}

@Composable
private fun ColumnScope.Details(alarm: Alarm) {
    val colors = AlarmTheme.colors
    Text(
        alarm.media.title,
        style = AlarmTheme.typography.titleXsmall,
        color = colors.text,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
    Text(
        stringResource(
            when (alarm.media) {
                is MusicMedia -> R.string.alarm_type_song
                is RingtoneMedia -> R.string.alarm_type_ringtone
                is TubeMedia -> R.string.alarm_type_youtube
            },
        ),
        Modifier.padding(top = 2.dp),
        style = AlarmTheme.typography.captionRegular,
        color = colors.sub,
    )
    if (alarm.memo.isNotBlank()) {
        Row(
            Modifier
                .padding(top = Space.s20)
                .background(colors.overlay, RoundedCornerShape(Radius.lg))
                .padding(horizontal = Space.s16, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(Space.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(DesignSystemR.drawable.designsystem_ic_memo), null, Modifier.size(18.dp), tint = colors.sub)
            Text(alarm.memo, Modifier.testTag("tv_memo"), style = AlarmTheme.typography.bodyMedium, color = colors.text)
        }
    }
}

/** 화면 가운데 강조색 번짐 (피그마 AccentGlow) */
private fun Modifier.ringGlow(color: Color) = drawBehind {
    val center = Offset(size.width / 2, 430.dp.toPx())
    val radius = 370.dp.toPx()
    drawCircle(
        Brush.radialGradient(
            0f to color.copy(alpha = 0.16f),
            1f to color.copy(alpha = 0f),
            center = center,
            radius = radius,
        ),
        radius,
        center,
    )
}

// 피그마 184dp 타일의 모서리 48dp 비율
private const val TILE_RADIUS_RATIO = 48f / 184f
