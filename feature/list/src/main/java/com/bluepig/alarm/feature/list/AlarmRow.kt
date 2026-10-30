package com.bluepig.alarm.feature.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.component.MediaTile
import com.bluepig.alarm.designsystem.component.MediaTileType
import com.bluepig.alarm.designsystem.component.Toggle
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import java.text.DateFormatSymbols
import java.util.Calendar

/** v2/AlarmRow: 메모 → 알람음 → 시각 → 요일 (제품 0003). 꺼진 알람은 흐리게 */
@Composable
internal fun AlarmRow(
    alarm: Alarm,
    onClick: () -> Unit,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    val time = alarmTimeText(alarm.timeInMillis)
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = Space.s24, vertical = Space.s16),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val (type, thumbnail) = alarm.media.tile()
            MediaTile(type, Modifier.alpha(if (alarm.isActive) 1f else 0.45f), thumbnail)
            Column(
                Modifier
                    .weight(1f)
                    .alpha(if (alarm.isActive) 1f else 0.4f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (alarm.memo.isNotBlank()) {
                    Text(
                        alarm.memo,
                        Modifier.testTag("tv_memo"),
                        style = typography.captionStrong,
                        color = colors.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    alarm.media.title,
                    style = typography.smallRegular,
                    color = colors.sub,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        time.clock,
                        Modifier.testTag("tv_time").alignByBaseline(),
                        style = typography.numTime,
                        color = colors.text,
                    )
                    time.meridiem?.let {
                        Text(it, Modifier.alignByBaseline(), style = typography.captionMedium, color = colors.sub)
                    }
                }
                RepeatDays(alarm.repeatWeek)
            }
            Toggle(
                checked = alarm.isActive,
                onCheckedChange = onActiveChange,
                modifier = Modifier
                    .testTag("switch_onOff")
                    .semantics { contentDescription = time.spoken },
            )
        }
        HorizontalDivider(color = colors.line)
    }
}

@Composable
private fun RepeatDays(repeatWeek: Set<Week>) {
    val colors = AlarmTheme.colors
    val style = AlarmTheme.typography.smallStrong
    if (repeatWeek.isEmpty()) {
        Text(stringResource(R.string.list_once), style = style, color = colors.sub)
        return
    }
    val locale = LocalConfiguration.current.locales[0]
    val names = remember(locale) { DateFormatSymbols.getInstance(locale).shortWeekdays }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Week.entries.forEach { week ->
            Text(names[week.code], style = style, color = if (week in repeatWeek) colors.accent else colors.dim)
        }
    }
}

private fun AlarmMedia.tile(): Pair<MediaTileType, String?> = when (this) {
    is MusicMedia -> MediaTileType.Song to thumbnail
    is RingtoneMedia -> MediaTileType.Ringtone to null
    is TubeMedia -> MediaTileType.YouTube to thumbnail
}

internal data class AlarmTimeText(val clock: String, val meridiem: String?, val spoken: String)

/** 12시간제는 07:00 + 오전/오후, 24시간제는 19:00 */
@Composable
private fun alarmTimeText(timeInMillis: Long): AlarmTimeText {
    val is24Hour = rememberIs24Hour()
    val locale = LocalConfiguration.current.locales[0]
    return remember(timeInMillis, is24Hour, locale) {
        val calendar = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
        val minute = calendar.get(Calendar.MINUTE)
        if (is24Hour) {
            val clock = "%02d:%02d".format(calendar.get(Calendar.HOUR_OF_DAY), minute)
            AlarmTimeText(clock, null, clock)
        } else {
            val hour = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
            val clock = "%02d:%02d".format(hour, minute)
            val meridiem = DateFormatSymbols.getInstance(locale).amPmStrings[calendar.get(Calendar.AM_PM)]
            AlarmTimeText(clock, meridiem, "$meridiem $clock")
        }
    }
}
