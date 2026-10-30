package com.bluepig.alarm.feature.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.AlarmSnackbarHost
import com.bluepig.alarm.designsystem.component.ButtonStyle
import com.bluepig.alarm.designsystem.component.MediaTile
import com.bluepig.alarm.designsystem.component.MediaTileType
import com.bluepig.alarm.designsystem.component.TopBar
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import java.text.DateFormatSymbols
import java.time.DayOfWeek
import java.time.format.TextStyle
import android.text.format.DateFormat as AndroidDateFormat
import com.bluepig.alarm.designsystem.R as DesignSystemR

/**
 * @param selectedMedia 알람음 설정 화면에서 고른 결과. 바뀔 때마다 편집 중인 알람에 넣음
 */
@Composable
fun AlarmEditRoute(
    selectedMedia: AlarmMedia?,
    onClose: () -> Unit,
    onSelectMedia: () -> Unit,
    onPreview: (Alarm) -> Unit,
    onSaved: (alarm: Alarm, isNew: Boolean) -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlarmEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val mediaRequired = stringResource(R.string.edit_media_required)
    val saveFailed = stringResource(R.string.edit_save_failed)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(selectedMedia) { selectedMedia?.let(viewModel::onMediaSelected) }
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AlarmEditEffect.Saved -> onSaved(effect.alarm, effect.isNew)
                    AlarmEditEffect.Deleted -> onDeleted()
                    is AlarmEditEffect.Preview -> onPreview(effect.alarm)
                    AlarmEditEffect.MediaRequired -> snackbarHostState.showSnackbar(mediaRequired)
                    AlarmEditEffect.SaveFailed -> snackbarHostState.showSnackbar(saveFailed)
                    AlarmEditEffect.NotFound -> onClose()
                }
            }
        }
    }
    AlarmEditScreen(
        uiState = uiState,
        actions = EditActions(
            onClose = onClose,
            onTimeChange = viewModel::onTimeChange,
            onDayToggle = viewModel::onDayToggle,
            onSelectMedia = onSelectMedia,
            onVolumeChange = viewModel::onVolumeChange,
            onVolumeAutoIncreaseChange = viewModel::onVolumeAutoIncreaseChange,
            onVibrationChange = viewModel::onVibrationChange,
            onMemoChange = viewModel::onMemoChange,
            onMemoTtsChange = viewModel::onMemoTtsChange,
            onDelete = viewModel::onDelete,
            onPreview = viewModel::onPreview,
            onSave = viewModel::onSave,
        ),
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

internal class EditActions(
    val onClose: () -> Unit,
    val onTimeChange: (Int, Int) -> Unit,
    val onDayToggle: (Week) -> Unit,
    val onSelectMedia: () -> Unit,
    val onVolumeChange: (Int) -> Unit,
    val onVolumeAutoIncreaseChange: (Boolean) -> Unit,
    val onVibrationChange: (Boolean) -> Unit,
    val onMemoChange: (String) -> Unit,
    val onMemoTtsChange: (Boolean) -> Unit,
    val onDelete: () -> Unit,
    val onPreview: () -> Unit,
    val onSave: () -> Unit,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AlarmEditScreen(
    uiState: AlarmEditUiState,
    actions: EditActions,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = AlarmTheme.colors
    val editing = uiState as? AlarmEditUiState.Editing
    Box(
        modifier
            .fillMaxSize()
            .background(colors.background)
            .semantics { testTagsAsResourceId = true },
    ) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(
                    if (editing?.draft?.isNew == false) R.string.edit_title_edit else R.string.edit_title_create,
                ),
                navigationIcon = DesignSystemR.drawable.designsystem_ic_close,
                navigationContentDescription = stringResource(R.string.edit_close),
                onNavigationClick = actions.onClose,
            )
            if (editing == null) return@Column
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                EditContent(editing, actions)
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(Space.s16),
                horizontalArrangement = Arrangement.spacedBy(Space.s8),
            ) {
                AlarmButton(
                    stringResource(R.string.edit_preview),
                    onClick = actions.onPreview,
                    modifier = Modifier.weight(1f),
                    style = ButtonStyle.Secondary,
                    icon = DesignSystemR.drawable.designsystem_ic_play,
                )
                AlarmButton(stringResource(R.string.edit_save), onClick = actions.onSave, modifier = Modifier.weight(1f))
            }
        }
        AlarmSnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp))
    }
}

@Composable
private fun EditContent(state: AlarmEditUiState.Editing, actions: EditActions) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    val draft = state.draft
    val context = LocalContext.current
    val is24Hour = remember(context) { AndroidDateFormat.is24HourFormat(context) }
    var showMemoDialog by rememberSaveable { mutableStateOf(false) }

    TimeWheel(
        hourOfDay = draft.hourOfDay,
        minute = draft.minute,
        is24Hour = is24Hour,
        onTimeChange = actions.onTimeChange,
        modifier = Modifier.padding(start = Space.s24, end = Space.s24, top = Space.s12, bottom = Space.s8),
    )
    Text(
        stringResource(R.string.edit_rings_in, durationText(state.remainingMinutes)),
        Modifier
            .fillMaxWidth()
            .padding(start = Space.s24, end = Space.s24, top = Space.s8, bottom = Space.s24)
            .testTag("tv_time_reminder"),
        style = typography.labelMedium,
        color = colors.accent,
        textAlign = TextAlign.Center,
    )
    Column(verticalArrangement = Arrangement.spacedBy(Space.s8)) {
        SettingsGroup { RepeatSection(draft.repeatWeek, actions.onDayToggle) }
        SettingsGroup {
            MediaPicker(draft.media, actions.onSelectMedia)
            Column(
                Modifier.padding(horizontal = Space.s24, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(Space.s12),
            ) {
                Text(stringResource(R.string.edit_volume), style = typography.bodyMedium, color = colors.text)
                VolumeSlider(draft.volume, state.maxVolume, actions.onVolumeChange, Modifier.testTag("seekbar_volume"))
            }
            ToggleRow(
                stringResource(R.string.edit_volume_auto_increase),
                draft.isVolumeAutoIncrease,
                actions.onVolumeAutoIncreaseChange,
            )
            ToggleRow(stringResource(R.string.edit_vibration), draft.hasVibration, actions.onVibrationChange)
        }
        SettingsGroup {
            ValueRow(
                label = stringResource(R.string.edit_memo),
                value = draft.memo,
                placeholder = stringResource(R.string.edit_memo_none),
                onClick = { showMemoDialog = true },
            )
            val canTts = draft.memo.isNotBlank()
            ToggleRow(
                stringResource(R.string.edit_memo_tts),
                draft.memoTtsEnabled,
                actions.onMemoTtsChange,
                enabled = canTts,
            )
            if (!canTts) {
                Text(
                    stringResource(R.string.edit_memo_tts_helper),
                    Modifier.padding(start = Space.s24, end = Space.s24, bottom = Space.s12),
                    style = typography.smallRegular,
                    color = colors.sub,
                )
            }
        }
        if (!draft.isNew) {
            SettingsGroup { DestructiveRow(stringResource(R.string.edit_delete), actions.onDelete) }
        }
    }
    if (showMemoDialog) {
        MemoDialog(
            initial = draft.memo,
            onDismiss = { showMemoDialog = false },
            onConfirm = {
                actions.onMemoChange(it)
                showMemoDialog = false
            },
        )
    }
}

@Composable
private fun RepeatSection(repeatWeek: Set<Week>, onDayToggle: (Week) -> Unit) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    val locale = LocalConfiguration.current.locales[0]
    val names = remember(locale) { DateFormatSymbols.getInstance(locale).shortWeekdays }
    Column(
        Modifier.padding(horizontal = Space.s24, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(Space.s12),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.edit_repeat), Modifier.weight(1f), style = typography.bodyMedium, color = colors.text)
            Text(
                repeatSummary(repeatWeek, names),
                style = typography.labelMedium,
                color = if (repeatWeek.isEmpty()) colors.sub else colors.accent,
            )
        }
        // 글꼴을 키워도 원 안에 들어가게 한 글자(영어 S M T…), 읽어 줄 때는 전체 이름
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Week.entries.forEach { week ->
                val day = week.dayOfWeek
                DayButton(
                    label = day.getDisplayName(TextStyle.NARROW, locale),
                    checked = week in repeatWeek,
                    onClick = { onDayToggle(week) },
                    modifier = Modifier.semantics { contentDescription = day.getDisplayName(TextStyle.FULL, locale) },
                )
            }
        }
    }
}

@Composable
private fun repeatSummary(repeatWeek: Set<Week>, names: Array<String>): String {
    val weekend = Week.entries.filter { it.isWeekend }.toSet()
    return when (repeatWeek) {
        emptySet<Week>() -> stringResource(R.string.edit_repeat_once)
        Week.entries.toSet() -> stringResource(R.string.edit_repeat_everyday)
        weekend -> stringResource(R.string.edit_repeat_weekends)
        Week.entries.toSet() - weekend -> stringResource(R.string.edit_repeat_weekdays)
        else -> Week.entries.filter { it in repeatWeek }.joinToString(" ") { names[it.code] }
    }
}

@Composable
private fun MediaPicker(media: AlarmMedia?, onClick: () -> Unit) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s24, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(Space.s12),
    ) {
        Text(stringResource(R.string.edit_alarm_sound), style = typography.bodyMedium, color = colors.text)
        Row(horizontalArrangement = Arrangement.spacedBy(Space.s12), verticalAlignment = Alignment.CenterVertically) {
            val (type, thumbnail, caption) = when (media) {
                null -> Triple(MediaTileType.Empty, null, R.string.edit_sound_types)
                is MusicMedia -> Triple(MediaTileType.Song, media.thumbnail, R.string.edit_type_song)
                is RingtoneMedia -> Triple(MediaTileType.Ringtone, null, R.string.edit_type_ringtone)
                is TubeMedia -> Triple(MediaTileType.YouTube, media.thumbnail, R.string.edit_type_youtube)
            }
            MediaTile(type, thumbnail = thumbnail, size = 52.dp, cornerRadius = 14.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    media?.title ?: stringResource(R.string.edit_select_sound),
                    Modifier.testTag("tv_media_title"),
                    style = typography.bodyStrong,
                    color = if (media == null) colors.accent else colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(stringResource(caption), style = typography.smallRegular, color = colors.sub)
            }
            Icon(
                painterResource(DesignSystemR.drawable.designsystem_ic_chevron),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = colors.sub,
            )
        }
    }
}

private val Week.dayOfWeek: DayOfWeek
    get() = DayOfWeek.of((code + 5) % 7 + 1) // Calendar.SUNDAY(1) → DayOfWeek.SUNDAY(7)

/** 하루 이상이면 일·시간, 한 시간 이상이면 시간·분, 그 밖은 분. 뒷부분이 0이면 뺌 */
@Composable
private fun durationText(minutes: Long): String {
    val days = (minutes / (24 * 60)).toInt()
    val hours = (minutes % (24 * 60) / 60).toInt()
    val mins = (minutes % 60).toInt()
    val day = stringResource(R.string.edit_duration_day, days)
    val hour = stringResource(R.string.edit_duration_hour, hours)
    val minute = stringResource(R.string.edit_duration_minute, mins)
    return when {
        days > 0 -> if (hours > 0) "$day $hour" else day
        hours > 0 -> if (mins > 0) "$hour $minute" else hour
        else -> minute
    }
}
