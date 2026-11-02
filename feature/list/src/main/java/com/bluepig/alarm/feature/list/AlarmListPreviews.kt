package com.bluepig.alarm.feature.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia

private val sampleAlarms = listOf(
    Alarm(
        id = 1, timeInMillis = 0, volume = 10, memo = "물 한 잔 마시기",
        media = MusicMedia("전선을 간다 - 국방부 군악대", "1", "", ""),
        repeatWeek = Week.entries.filterNot { it.isWeekend }.toSet(),
    ),
    Alarm(
        id = 2, timeInMillis = 0, volume = 10,
        media = RingtoneMedia(1, "Galaxy Bells", ""),
        repeatWeek = Week.entries.filter { it.isWeekend }.toSet(),
    ),
    Alarm(
        id = 3, timeInMillis = 0, volume = 10, isActive = false, memo = "헬스장 가기 전에 스트레칭하고 물 챙기기",
        media = TubeMedia("v", "", "아침을 깨우는 노래 모음 1시간 연속 재생 플레이리스트"),
    ),
)

@Composable
private fun PreviewScreen(state: AlarmListUiState) {
    AlarmTheme {
        AlarmListScreen(state, {}, {}, { _, _ -> }, nativeAd = { modifier ->
            Box(modifier.height(77.dp), contentAlignment = Alignment.Center) { Text("native ad") }
        })
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AlarmsPreview() =
    PreviewScreen(AlarmListUiState.Alarms(sampleAlarms, NextAlarmUi(System.currentTimeMillis(), 7 * 60 + 30)))

@Preview
@Composable
private fun AllOffPreview() = PreviewScreen(AlarmListUiState.Alarms(sampleAlarms, null))

@Preview
@Preview(locale = "en", fontScale = 1.5f)
@Composable
private fun EmptyPreview() = PreviewScreen(AlarmListUiState.Empty)

@Preview(widthDp = 360)
@Composable
private fun SummaryDaysPreview() = AlarmTheme {
    ListSummary(NextAlarmUi(System.currentTimeMillis() + 3 * 86_400_000L, 3 * 24 * 60 + 5L), Modifier.fillMaxWidth())
}
