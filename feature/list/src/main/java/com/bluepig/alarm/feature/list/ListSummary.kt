package com.bluepig.alarm.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Space
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.text.format.DateFormat as AndroidDateFormat

/** v2/ListSummary. 다음 알람까지 남은 시간 + 언제, 모두 꺼져 있으면 안내 문구 */
@Composable
internal fun ListSummary(next: NextAlarmUi?, modifier: Modifier = Modifier) {
    val colors = AlarmTheme.colors
    val typography = AlarmTheme.typography
    Column(
        modifier
            .fillMaxWidth()
            .padding(start = Space.s24, end = Space.s24, top = Space.s32, bottom = Space.s24)
            .testTag("tv_alarm_state"),
        verticalArrangement = Arrangement.spacedBy(Space.s4),
    ) {
        if (next == null) {
            Text(stringResource(R.string.list_all_off_title), style = typography.titleLarge, color = colors.text)
            Text(stringResource(R.string.list_all_off_body), style = typography.bodySmall, color = colors.sub)
            return@Column
        }
        Text(stringResource(R.string.list_next_alarm_in), style = typography.labelMedium, color = colors.sub)
        Row {
            remainingParts(next.remainingMinutes).forEachIndexed { index, (value, unit) ->
                if (index > 0) Spacer(Modifier.width(14.dp))
                Text(value.toString(), Modifier.alignByBaseline(), style = typography.numDisplay, color = colors.text)
                Spacer(Modifier.width(Space.s4))
                Text(stringResource(unit), Modifier.alignByBaseline(), style = typography.unit, color = colors.sub)
            }
        }
        Text(whenText(next.timeInMillis), style = typography.bodySmall, color = colors.sub)
    }
}

/** 하루 이상이면 일·시간, 한 시간 이상이면 시간·분, 그 밖은 분 */
private fun remainingParts(minutes: Long): List<Pair<Long, Int>> {
    val days = minutes / (24 * 60)
    val hours = minutes % (24 * 60) / 60
    val mins = minutes % 60
    return when {
        days > 0 -> listOf(days to R.string.list_unit_day, hours to R.string.list_unit_hour)
        hours > 0 -> listOf(hours to R.string.list_unit_hour, mins to R.string.list_unit_minute)
        else -> listOf(mins to R.string.list_unit_minute)
    }
}

/** 오늘 오전 7:00 / 내일(금) 오전 7:00 / 10월 31일 (토) 오전 7:00 */
@Composable
private fun whenText(timeInMillis: Long): String {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val date = Date(timeInMillis)
    val clock = remember(timeInMillis, locale) { AndroidDateFormat.getTimeFormat(context).format(date) }
    val target = Calendar.getInstance().apply { time = date }
    val today = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DATE, 1) }
    return when {
        target.isSameDay(today) -> stringResource(R.string.list_when_today, clock)
        target.isSameDay(tomorrow) ->
            stringResource(R.string.list_when_tomorrow, SimpleDateFormat("EEE", locale).format(date), clock)

        else -> {
            val pattern = AndroidDateFormat.getBestDateTimePattern(locale, "MMMdEEE")
            stringResource(R.string.list_when_date, SimpleDateFormat(pattern, locale).format(date), clock)
        }
    }
}

private fun Calendar.isSameDay(other: Calendar) =
    get(Calendar.YEAR) == other.get(Calendar.YEAR) && get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
