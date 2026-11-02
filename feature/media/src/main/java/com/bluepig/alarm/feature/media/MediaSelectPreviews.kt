package com.bluepig.alarm.feature.media

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo

private val noActions = MediaActions({}, {}, {}, {}, {}, {}, {}, {})

@Composable
private fun PreviewScreen(state: MediaSelectUiState) = AlarmTheme { MediaSelectScreen(state, noActions) }

@Preview
@Composable
private fun SongIdlePreview() = PreviewScreen(MediaSelectUiState())

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SongResultsPreview() = PreviewScreen(
    MediaSelectUiState(
        song = SearchState.Results(
            listOf("전선을 간다", "전선을 간다 - 국방부 군악대", "전선을 간다 (Remastered)").mapIndexed { i, title ->
                MusicInfo("", "$i", "", title)
            },
            isLoadingMore = true,
        ),
    ),
)

@Preview
@Composable
private fun SongEmptyPreview() = PreviewScreen(MediaSelectUiState(song = SearchState.Results(emptyList())))

@Preview(locale = "en", fontScale = 1.5f)
@Composable
private fun SongErrorPreview() = PreviewScreen(MediaSelectUiState(song = SearchState.Failed(SearchError.Network)))

@Preview
@Composable
private fun RingtonePreview() = PreviewScreen(
    MediaSelectUiState(
        tab = MediaTab.Ringtone,
        ringtone = RingtoneState.Loaded(listOf("Basic Bell", "Beep Once", "Chimes").mapIndexed { i, t -> RingtoneMedia(i.toLong(), t, "") }),
    ),
)

@Preview
@Composable
private fun YoutubePreview() = PreviewScreen(
    MediaSelectUiState(
        tab = MediaTab.YouTube,
        youtube = SearchState.Results(listOf(TubeMedia("v", "", "아침을 깨우는 노래 모음 1시간 연속 재생"))),
    ),
)
