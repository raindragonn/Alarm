package com.bluepig.alarm.feature.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.NetworkUnavailableException
import com.bluepig.alarm.domain.result.TubeAuthRequiredException
import com.bluepig.alarm.domain.usecase.GetAppConfig
import com.bluepig.alarm.domain.usecase.GetMusicMedia
import com.bluepig.alarm.domain.usecase.GetRingtones
import com.bluepig.alarm.domain.usecase.GetTubeAccount
import com.bluepig.alarm.domain.usecase.GetTubeMediaList
import com.bluepig.alarm.domain.usecase.SaveTubeAccount
import com.bluepig.alarm.domain.usecase.SearchMusicInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MediaTab { Song, Ringtone, YouTube }

enum class SearchError { Network, Other }

/** 검색 탭(노래·유튜브) 한 칸의 상태 */
sealed interface SearchState<out T> {
    /** 아직 검색하지 않음 */
    data object Idle : SearchState<Nothing>
    data object Loading : SearchState<Nothing>
    data class Results<T>(val items: List<T>, val isLoadingMore: Boolean = false) : SearchState<T>
    data class Failed(val error: SearchError) : SearchState<Nothing>
}

sealed interface RingtoneState {
    data object Loading : RingtoneState
    data class Loaded(val items: List<RingtoneMedia>) : RingtoneState
    data object Failed : RingtoneState
}

/** 미리듣기 바텀시트. 노래는 재생할 파일 주소를 받는 동안 [Loading] */
sealed interface PreviewState {
    data class Loading(val title: String) : PreviewState
    data class Ready(val media: AlarmMedia) : PreviewState
}

data class MediaSelectUiState(
    val tabs: List<MediaTab> = MediaTab.entries,
    val tab: MediaTab = MediaTab.Song,
    val song: SearchState<MusicInfo> = SearchState.Idle,
    val ringtone: RingtoneState = RingtoneState.Loading,
    val youtube: SearchState<TubeMedia> = SearchState.Idle,
    val onlyYoutubeLink: Boolean = false,
    val preview: PreviewState? = null,
    val showYoutubeNotice: Boolean = false,
)

sealed interface MediaSelectEffect {
    data class Selected(val media: AlarmMedia) : MediaSelectEffect
    data class Searched(val tab: MediaTab, val query: String) : MediaSelectEffect

    /** 계정 선택 화면을 띄움 */
    data object ChooseAccount : MediaSelectEffect

    /** YouTube 재인증. [resolution]은 Android Intent (기술 0016) */
    data class Reauthorize(val resolution: Any) : MediaSelectEffect
    data object PreviewFailed : MediaSelectEffect
}

@HiltViewModel
class MediaSelectViewModel @Inject constructor(
    getAppConfig: GetAppConfig,
    private val _getRingtones: GetRingtones,
    private val _searchMusicInfo: SearchMusicInfo,
    private val _getMusicMedia: GetMusicMedia,
    private val _getTubeMediaList: GetTubeMediaList,
    private val _getTubeAccount: GetTubeAccount,
    private val _saveTubeAccount: SaveTubeAccount,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MediaSelectUiState())
    val uiState: StateFlow<MediaSelectUiState> = _uiState.asStateFlow()

    private val _effect = Channel<MediaSelectEffect>(Channel.BUFFERED)
    val effect: Flow<MediaSelectEffect> = _effect.receiveAsFlow()

    private var _songQuery = ""
    private var _youtubeQuery = ""
    private var _songJob: Job? = null
    private var _youtubeJob: Job? = null
    private var _previewJob: Job? = null

    init {
        viewModelScope.launch {
            val config = getAppConfig()
            val tabs = MediaTab.entries.filter { it != MediaTab.YouTube || config.showYoutubeSearch }
            _uiState.update { it.copy(tabs = tabs, onlyYoutubeLink = config.showOnlyYoutubeLink) }
        }
        viewModelScope.launch {
            val ringtone = _getRingtones().fold({ RingtoneState.Loaded(it) }, { RingtoneState.Failed })
            _uiState.update { it.copy(ringtone = ringtone) }
        }
    }

    fun onTabSelect(tab: MediaTab) = _uiState.update { it.copy(tab = tab) }

    fun onSearchSong(query: String) {
        if (query.isBlank()) return
        _songQuery = query
        _songJob?.cancel()
        _uiState.update { it.copy(song = SearchState.Loading) }
        _songJob = viewModelScope.launch {
            _effect.send(MediaSelectEffect.Searched(MediaTab.Song, query))
            val result = _searchMusicInfo(query)
            _uiState.update { it.copy(song = result.toSearchState()) }
        }
    }

    /** 결과 끝까지 내리면 같은 검색어로 다음 쪽 (UseCase가 이어 붙여 돌려줌) */
    fun onLoadMoreSong() {
        val current = _uiState.value.song as? SearchState.Results ?: return
        if (current.isLoadingMore || _songJob?.isActive == true) return
        _uiState.update { it.copy(song = current.copy(isLoadingMore = true)) }
        _songJob = viewModelScope.launch {
            val items = _searchMusicInfo(_songQuery).getOrNull() ?: current.items
            _uiState.update { it.copy(song = SearchState.Results(items)) }
        }
    }

    fun onSearchYoutube(query: String) {
        if (query.isBlank()) return
        _youtubeQuery = query
        viewModelScope.launch {
            if (_getTubeAccount().isNullOrBlank()) {
                _uiState.update { it.copy(showYoutubeNotice = true) }
            } else {
                searchYoutube()
            }
        }
    }

    fun onLoadMoreYoutube() {
        val current = _uiState.value.youtube as? SearchState.Results ?: return
        if (current.isLoadingMore || _youtubeJob?.isActive == true) return
        _uiState.update { it.copy(youtube = current.copy(isLoadingMore = true)) }
        _youtubeJob = viewModelScope.launch {
            val items = _getTubeMediaList(_youtubeQuery).getOrNull() ?: current.items
            _uiState.update { it.copy(youtube = SearchState.Results(items)) }
        }
    }

    fun onYoutubeNoticeAgree() {
        _uiState.update { it.copy(showYoutubeNotice = false) }
        viewModelScope.launch { _effect.send(MediaSelectEffect.ChooseAccount) }
    }

    fun onYoutubeNoticeDismiss() = _uiState.update { it.copy(showYoutubeNotice = false) }

    fun onAccountChosen(accountName: String) {
        viewModelScope.launch {
            _saveTubeAccount(accountName)
            searchYoutube()
        }
    }

    /** 재인증을 마치면 다시 검색. 취소하면 아무것도 하지 않음 (M3: 화면에 돌아올 때마다 다시 띄우지 않음) */
    fun onReauthorized() = searchYoutube()

    fun onMusicClick(musicInfo: MusicInfo) {
        _previewJob?.cancel()
        _uiState.update { it.copy(preview = PreviewState.Loading(musicInfo.title)) }
        _previewJob = viewModelScope.launch {
            _getMusicMedia(musicInfo)
                .onSuccess { media -> _uiState.update { it.copy(preview = PreviewState.Ready(media)) } }
                .onFailure {
                    _uiState.update { it.copy(preview = null) }
                    _effect.send(MediaSelectEffect.PreviewFailed)
                }
        }
    }

    fun onMediaClick(media: AlarmMedia) = _uiState.update { it.copy(preview = PreviewState.Ready(media)) }

    fun onPreviewDismiss() {
        _previewJob?.cancel()
        _uiState.update { it.copy(preview = null) }
    }

    fun onSelect() {
        val media = (_uiState.value.preview as? PreviewState.Ready)?.media ?: return
        viewModelScope.launch { _effect.send(MediaSelectEffect.Selected(media)) }
    }

    private fun searchYoutube() {
        val query = _youtubeQuery.takeIf { it.isNotBlank() } ?: return
        _youtubeJob?.cancel()
        _uiState.update { it.copy(youtube = SearchState.Loading) }
        _youtubeJob = viewModelScope.launch {
            _effect.send(MediaSelectEffect.Searched(MediaTab.YouTube, query))
            val result = _getTubeMediaList(query)
            val auth = result.exceptionOrNull() as? TubeAuthRequiredException
            if (auth != null) {
                // 저장된 계정으로 다시 인증해야 함. 취소하면 다음 검색 때 연동 안내부터
                _saveTubeAccount("")
                _uiState.update { it.copy(youtube = SearchState.Idle) }
                _effect.send(MediaSelectEffect.Reauthorize(auth.resolution))
            } else {
                _uiState.update { it.copy(youtube = result.toSearchState()) }
            }
        }
    }

    private fun <T> Result<List<T>>.toSearchState(): SearchState<T> = fold(
        onSuccess = { SearchState.Results(it) },
        onFailure = { SearchState.Failed(if (it is NetworkUnavailableException) SearchError.Network else SearchError.Other) },
    )
}
