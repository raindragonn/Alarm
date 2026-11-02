package com.bluepig.alarm.feature.media

import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.fake.FakeAppConfigRepository
import com.bluepig.alarm.domain.fake.FakeMusicInfoRepository
import com.bluepig.alarm.domain.fake.FakeRingtoneRepository
import com.bluepig.alarm.domain.fake.FakeTubeRepository
import com.bluepig.alarm.domain.fake.allOnConfig
import com.bluepig.alarm.domain.result.NetworkUnavailableException
import com.bluepig.alarm.domain.result.TubeAuthRequiredException
import com.bluepig.alarm.domain.usecase.GetAppConfig
import com.bluepig.alarm.domain.usecase.GetMusicMedia
import com.bluepig.alarm.domain.usecase.GetRingtones
import com.bluepig.alarm.domain.usecase.GetTubeAccount
import com.bluepig.alarm.domain.usecase.GetTubeMediaList
import com.bluepig.alarm.domain.usecase.SaveTubeAccount
import com.bluepig.alarm.domain.usecase.SearchMusicInfo
import com.bluepig.alarm.domain.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MediaSelectViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val song = MusicInfo("https://4shared/page", "1", "thumb", "전선을 간다")
    private val video = TubeMedia("v1", "thumb", "아침 노래")

    private fun viewModel(
        music: FakeMusicInfoRepository = FakeMusicInfoRepository(listOf(song)),
        tube: FakeTubeRepository = FakeTubeRepository(searchResult = listOf(video), accountName = "me@gmail.com"),
        config: FakeAppConfigRepository = FakeAppConfigRepository(allOnConfig.copy(showOnlyYoutubeLink = false)),
    ) = MediaSelectViewModel(
        GetAppConfig(config),
        GetRingtones(FakeRingtoneRepository()),
        SearchMusicInfo(music),
        GetMusicMedia(music),
        GetTubeMediaList(tube, config),
        GetTubeAccount(tube),
        SaveTubeAccount(tube),
    )

    @Test
    fun `원격 설정에서 유튜브 검색을 끄면 유튜브 탭을 숨김`() = runTest {
        val viewModel = viewModel(config = FakeAppConfigRepository(allOnConfig.copy(showYoutubeSearch = false)))
        advanceUntilIdle()

        assertEquals(listOf(MediaTab.Song, MediaTab.Ringtone), viewModel.uiState.value.tabs)
    }

    @Test
    fun `노래 검색 결과`() = runTest {
        val viewModel = viewModel()

        viewModel.onSearchSong("전선을 간다")
        advanceUntilIdle()

        assertEquals(SearchState.Results(listOf(song)), viewModel.uiState.value.song)
    }

    @Test
    fun `인터넷이 끊기면 연결 오류`() = runTest {
        val viewModel = viewModel(music = FakeMusicInfoRepository(error = NetworkUnavailableException(IOException())))

        viewModel.onSearchSong("전선을 간다")
        advanceUntilIdle()

        assertEquals(SearchState.Failed(SearchError.Network), viewModel.uiState.value.song)
    }

    @Test
    fun `노래를 누르면 파일 주소를 받아 미리듣기, 선택하면 알림`() = runTest {
        val viewModel = viewModel(music = FakeMusicInfoRepository(listOf(song), fileUrl = "https://4shared/file.mp3"))

        viewModel.onMusicClick(song)
        assertEquals(PreviewState.Loading("전선을 간다"), viewModel.uiState.value.preview)
        advanceUntilIdle()
        val media = (viewModel.uiState.value.preview as PreviewState.Ready).media as MusicMedia
        viewModel.onSelect()

        assertEquals("https://4shared/file.mp3", media.fileUrl)
        assertEquals(MediaSelectEffect.Selected(media), viewModel.effect.first { it is MediaSelectEffect.Selected })
    }

    @Test
    fun `계정이 없으면 유튜브 연동 안내, 동의하면 계정 선택 후 검색`() = runTest {
        val tube = FakeTubeRepository(searchResult = listOf(video))
        val viewModel = viewModel(tube = tube)

        viewModel.onSearchYoutube("아침")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showYoutubeNotice)

        viewModel.onYoutubeNoticeAgree()
        assertEquals(MediaSelectEffect.ChooseAccount, viewModel.effect.first())
        viewModel.onAccountChosen("me@gmail.com")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showYoutubeNotice)
        assertEquals("me@gmail.com", tube.accountName)
        assertEquals(SearchState.Results(listOf(video)), viewModel.uiState.value.youtube)
    }

    // M3: 재인증을 상태로 두면 화면에 돌아올 때마다 다시 떴음
    @Test
    fun `재인증이 필요하면 한 번만 요청하고 계정을 지움`() = runTest {
        val tube = FakeTubeRepository(accountName = "me@gmail.com", searchError = TubeAuthRequiredException("intent"))
        val viewModel = viewModel(tube = tube)

        viewModel.onSearchYoutube("아침")
        advanceUntilIdle()

        assertEquals(
            MediaSelectEffect.Reauthorize("intent"),
            viewModel.effect.first { it is MediaSelectEffect.Reauthorize },
        )
        assertEquals(SearchState.Idle, viewModel.uiState.value.youtube)
        assertEquals("", tube.accountName)
    }
}
