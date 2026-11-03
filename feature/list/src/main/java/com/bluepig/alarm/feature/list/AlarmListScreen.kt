package com.bluepig.alarm.feature.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.AlarmSnackbarHost
import com.bluepig.alarm.designsystem.component.NoticeCard
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.designsystem.R as DesignSystemR

/** @param canDrawOverlays 다른 앱 위에 표시 권한. 없으면 요약 아래 안내 카드 (제품 0017) */
@Composable
fun AlarmListRoute(
    onAddAlarm: () -> Unit,
    onAlarmClick: (Alarm) -> Unit,
    onAlarmSaved: (Alarm) -> Unit,
    onAlarmsLoaded: (List<Alarm>) -> Unit,
    nativeAd: @Composable (Modifier) -> Unit,
    canDrawOverlays: Boolean,
    onOpenOverlaySetting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlarmListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveFailed = stringResource(R.string.list_save_failed)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AlarmListEffect.AlarmSaved -> onAlarmSaved(effect.alarm)
                    AlarmListEffect.SaveFailed -> snackbarHostState.showSnackbar(saveFailed)
                }
            }
        }
    }
    (uiState as? AlarmListUiState.Alarms)?.let { state ->
        LaunchedEffect(state.alarms) { onAlarmsLoaded(state.alarms) }
    }
    AlarmListScreen(
        uiState = uiState,
        onAddAlarm = onAddAlarm,
        onAlarmClick = onAlarmClick,
        onActiveChange = viewModel::onActiveChange,
        nativeAd = nativeAd,
        canDrawOverlays = canDrawOverlays,
        onOpenOverlaySetting = onOpenOverlaySetting,
        onDismissOverlayNotice = viewModel::onDismissOverlayNotice,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AlarmListScreen(
    uiState: AlarmListUiState,
    onAddAlarm: () -> Unit,
    onAlarmClick: (Alarm) -> Unit,
    onActiveChange: (Alarm, Boolean) -> Unit,
    nativeAd: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    canDrawOverlays: Boolean = true,
    onOpenOverlaySetting: () -> Unit = {},
    onDismissOverlayNotice: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = AlarmTheme.colors
    Box(
        modifier
            .fillMaxSize()
            .background(colors.background)
            .accentGlow(colors.accent)
            .semantics { testTagsAsResourceId = true },
    ) {
        when (uiState) {
            AlarmListUiState.Loading -> Unit
            // 알람이 없으면 목록 네이티브 없이 하단 한 줄 광고만 (제품 0015)
            AlarmListUiState.Empty -> EmptyState(onAddAlarm, Modifier.fillMaxSize())

            is AlarmListUiState.Alarms -> {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .testTag("rv_alarm"),
                    contentPadding = PaddingValues(bottom = FabSpace),
                ) {
                    item(key = "summary", contentType = "summary") {
                        ListSummary(uiState.next)
                        uiState.overlayNotice?.takeUnless { canDrawOverlays }?.let {
                            OverlayNoticeCard(it, onOpenOverlaySetting, onDismissOverlayNotice)
                        }
                        HorizontalDivider(color = colors.line)
                    }
                    item(key = "ad", contentType = "ad") { nativeAd(Modifier.fillMaxWidth()) }
                    items(uiState.alarms, key = { it.id ?: 0L }, contentType = { "alarm" }) { alarm ->
                        AlarmRow(
                            alarm = alarm,
                            onClick = { onAlarmClick(alarm) },
                            onActiveChange = { onActiveChange(alarm, it) },
                        )
                    }
                }
                AddFab(
                    onAddAlarm,
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Space.s20),
                )
            }
        }
        AlarmSnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }
}

/** 오른쪽 위 강조색 번짐 (피그마 AccentGlow: 지름 260, 흐림 52, 불투명도 18%) */
private fun Modifier.accentGlow(color: Color) = drawBehind {
        val center = Offset(size.width - 70.dp.toPx(), 10.dp.toPx())
        val radius = 234.dp.toPx()
        drawCircle(
            Brush.radialGradient(
                0f to color.copy(alpha = 0.18f),
                0.45f to color.copy(alpha = 0.12f),
                1f to color.copy(alpha = 0f),
                center = center,
                radius = radius,
            ),
            radius = radius,
            center = center,
        )
    }

private val FabSpace = 104.dp

@Composable
private fun AddFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(64.dp),
        shape = CircleShape,
        color = AlarmTheme.colors.accent,
        contentColor = AlarmTheme.colors.onAccent,
        shadowElevation = 6.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painterResource(DesignSystemR.drawable.designsystem_ic_add),
                contentDescription = stringResource(R.string.list_create_alarm),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun OverlayNoticeCard(notice: OverlayNotice, onAllow: () -> Unit, onDismiss: () -> Unit) {
    val youtube = notice == OverlayNotice.YouTube
    NoticeCard(
        title = stringResource(if (youtube) R.string.list_overlay_youtube_title else R.string.list_overlay_title),
        body = stringResource(if (youtube) R.string.list_overlay_youtube_body else R.string.list_overlay_body),
        actionLabel = stringResource(R.string.list_overlay_allow),
        onAction = onAllow,
        modifier = Modifier.padding(start = Space.s16, end = Space.s16, bottom = Space.s16),
        dismissLabel = if (youtube) null else stringResource(R.string.list_overlay_dismiss),
        onDismiss = onDismiss,
    )
}

@Composable
private fun EmptyState(onAddAlarm: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = Space.s32, end = Space.s32, bottom = Space.s24),
        verticalArrangement = Arrangement.spacedBy(Space.s12, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(104.dp)
                .background(colors.fill, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(DesignSystemR.drawable.designsystem_ic_alarm),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colors.accent,
            )
        }
        Spacer(Modifier.height(Space.s8))
        Text(stringResource(R.string.list_empty_title), style = typography.titleMedium, color = colors.text)
        Text(
            stringResource(R.string.list_empty_body),
            style = typography.bodyRegular,
            color = colors.sub,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Space.s12))
        AlarmButton(
            text = stringResource(R.string.list_add_alarm),
            onClick = onAddAlarm,
            icon = DesignSystemR.drawable.designsystem_ic_add,
        )
    }
}

@Composable
internal fun rememberIs24Hour(): Boolean {
    val context = LocalContext.current
    return remember(context) { android.text.format.DateFormat.is24HourFormat(context) }
}
