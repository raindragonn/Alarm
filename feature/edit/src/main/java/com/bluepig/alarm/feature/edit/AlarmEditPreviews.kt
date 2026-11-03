package com.bluepig.alarm.feature.edit

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia

private val noActions = EditActions({}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})

private val newDraft = AlarmDraft(
    id = null, hourOfDay = 7, minute = 0, repeatWeek = emptySet(), media = null, volume = 11,
    isVolumeAutoIncrease = true, hasVibration = false, memo = "", memoTtsEnabled = false,
)

@Composable
private fun PreviewScreen(draft: AlarmDraft, canDrawOverlays: Boolean = true) = AlarmTheme {
    AlarmEditScreen(
        AlarmEditUiState.Editing(draft, maxVolume = 15, remainingMinutes = 7 * 60 + 30),
        noActions,
        canDrawOverlays = canDrawOverlays,
    )
}

@Preview(heightDp = 1100)
@Composable
private fun CreatePreview() = PreviewScreen(newDraft)

@Preview(heightDp = 1150)
@Preview(heightDp = 1150, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditPreview() = PreviewScreen(
    newDraft.copy(
        id = 1,
        repeatWeek = Week.entries.filterNot { it.isWeekend }.toSet(),
        media = MusicMedia("전선을 간다 - 국방부 군악대", "1", "", ""),
        memo = "물 한 잔 마시기",
        memoTtsEnabled = true,
    ),
)

// 다른 앱 위에 표시 권한이 없는 유튜브 알람 (제품 0017)
@Preview(heightDp = 1250)
@Preview(heightDp = 1250, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun YoutubeNoOverlayPreview() = PreviewScreen(
    newDraft.copy(media = TubeMedia(videoId = "1", thumbnail = "", title = "cozy autumn music 🍂 lofi beats")),
    canDrawOverlays = false,
)

@Preview(heightDp = 1300, locale = "en", fontScale = 1.5f)
@Composable
private fun LargeFontPreview() = PreviewScreen(newDraft)
