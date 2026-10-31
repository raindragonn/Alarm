package com.bluepig.alarm.feature.media

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.AlarmSnackbarHost
import com.bluepig.alarm.designsystem.component.ButtonStyle
import com.bluepig.alarm.designsystem.component.TopBar
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.google.android.gms.common.AccountPicker
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import com.bluepig.alarm.designsystem.R as DesignSystemR

/**
 * @param onSelected 고른 알람음. 전면 광고를 거쳐 편집 화면으로 돌려주는 것은 app이 함
 * @param onSearched 분석 로그용
 */
@Composable
fun MediaSelectRoute(
    onBack: () -> Unit,
    onSelected: (AlarmMedia) -> Unit,
    onSearched: (tab: MediaTab, query: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MediaSelectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val previewFailed = stringResource(R.string.media_preview_failed)
    val accountLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)?.let(viewModel::onAccountChosen)
    }
    val reauthLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) viewModel.onReauthorized()
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is MediaSelectEffect.Selected -> onSelected(effect.media)
                    is MediaSelectEffect.Searched -> onSearched(effect.tab, effect.query)
                    MediaSelectEffect.ChooseAccount -> accountLauncher.launch(chooseAccountIntent())
                    is MediaSelectEffect.Reauthorize -> (effect.resolution as? Intent)?.let(reauthLauncher::launch)
                    MediaSelectEffect.PreviewFailed -> snackbarHostState.showSnackbar(previewFailed)
                }
            }
        }
    }
    MediaSelectScreen(
        uiState = uiState,
        actions = MediaActions(
            onBack = onBack,
            onTabSelect = viewModel::onTabSelect,
            onSearchSong = viewModel::onSearchSong,
            onLoadMoreSong = viewModel::onLoadMoreSong,
            onSearchYoutube = viewModel::onSearchYoutube,
            onLoadMoreYoutube = viewModel::onLoadMoreYoutube,
            onMusicClick = viewModel::onMusicClick,
            onMediaClick = viewModel::onMediaClick,
        ),
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
    uiState.preview?.let { PreviewSheet(it, viewModel::onPreviewDismiss, viewModel::onSelect) }
    if (uiState.showYoutubeNotice) {
        YoutubeNoticeDialog(viewModel::onYoutubeNoticeAgree, viewModel::onYoutubeNoticeDismiss)
    }
}

// GoogleAccountCredential.newChooseAccountIntent와 같은 값 (credential은 core:network에 있음)
private fun chooseAccountIntent(): Intent = AccountPicker.newChooseAccountIntent(
    AccountPicker.AccountChooserOptions.Builder()
        .setAllowableAccountsTypes(listOf("com.google"))
        .setAlwaysShowAccountPicker(true)
        .build(),
)

internal class MediaActions(
    val onBack: () -> Unit,
    val onTabSelect: (MediaTab) -> Unit,
    val onSearchSong: (String) -> Unit,
    val onLoadMoreSong: () -> Unit,
    val onSearchYoutube: (String) -> Unit,
    val onLoadMoreYoutube: () -> Unit,
    val onMusicClick: (MusicInfo) -> Unit,
    val onMediaClick: (AlarmMedia) -> Unit,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun MediaSelectScreen(
    uiState: MediaSelectUiState,
    actions: MediaActions,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = AlarmTheme.colors
    Box(
        modifier
            .fillMaxSize()
            .background(colors.background)
            .semantics { testTagsAsResourceId = true },
    ) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                title = stringResource(R.string.media_title),
                navigationIcon = DesignSystemR.drawable.designsystem_ic_back,
                navigationContentDescription = stringResource(R.string.media_back),
                onNavigationClick = actions.onBack,
            )
            SegmentedControl(
                uiState.tabs,
                uiState.tab,
                actions.onTabSelect,
                Modifier.padding(start = Space.s16, end = Space.s16, top = Space.s4, bottom = Space.s12),
            )
            when (uiState.tab) {
                MediaTab.Song -> SearchTab(
                    state = uiState.song,
                    placeholder = stringResource(R.string.media_song_hint),
                    idleTitle = stringResource(R.string.media_song_idle_title),
                    idleBody = stringResource(R.string.media_song_idle_body),
                    listTag = "rv_search",
                    onSearch = actions.onSearchSong,
                    onLoadMore = actions.onLoadMoreSong,
                ) { item -> SongRow(item.title, item.thumbnail) { actions.onMusicClick(item) } }

                MediaTab.Ringtone -> RingtoneTab(uiState.ringtone, actions.onMediaClick)
                MediaTab.YouTube -> SearchTab(
                    state = uiState.youtube,
                    placeholder = stringResource(
                        if (uiState.onlyYoutubeLink) R.string.media_youtube_link_hint else R.string.media_youtube_hint,
                    ),
                    idleTitle = stringResource(R.string.media_youtube_idle_title),
                    idleBody = stringResource(
                        if (uiState.onlyYoutubeLink) R.string.media_youtube_link_idle_body else R.string.media_youtube_idle_body,
                    ),
                    listTag = "rv_tube",
                    onSearch = actions.onSearchYoutube,
                    onLoadMore = actions.onLoadMoreYoutube,
                ) { item -> YoutubeRow(item.title, item.thumbnail) { actions.onMediaClick(item) } }
            }
        }
        AlarmSnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }
}

/** v2/SegmentedControl. 선택 = surface + label/strong */
@Composable
private fun SegmentedControl(
    tabs: List<MediaTab>,
    selected: MediaTab,
    onSelect: (MediaTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AlarmTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .background(colors.fill, RoundedCornerShape(Radius.md))
            .padding(Space.s4),
        horizontalArrangement = Arrangement.spacedBy(Space.s4),
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selected
            Box(
                Modifier
                    .weight(1f)
                    .background(if (isSelected) colors.surface else colors.fill, RoundedCornerShape(Radius.sm))
                    .selectable(isSelected, role = Role.Tab, onClick = { onSelect(tab) })
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(tab.label),
                    style = if (isSelected) AlarmTheme.typography.labelStrong else AlarmTheme.typography.labelMedium,
                    color = if (isSelected) colors.text else colors.sub,
                    maxLines = 1,
                )
            }
        }
    }
}

private val MediaTab.label
    get() = when (this) {
        MediaTab.Song -> R.string.media_type_song
        MediaTab.Ringtone -> R.string.media_type_ringtone
        MediaTab.YouTube -> R.string.media_type_youtube
    }

@Composable
private fun <T> SearchTab(
    state: SearchState<T>,
    placeholder: String,
    idleTitle: String,
    idleBody: String,
    listTag: String,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    row: @Composable (T) -> Unit,
) {
    var query by rememberSaveable(listTag) { mutableStateOf("") }
    SearchField(
        query = query,
        onQueryChange = { query = it },
        placeholder = placeholder,
        onSearch = { onSearch(query) },
        modifier = Modifier.padding(start = Space.s16, end = Space.s16, bottom = Space.s12),
    )
    ResultsBox {
        when (state) {
            SearchState.Idle -> CenterMessage(idleTitle, idleBody, Modifier.align(Alignment.Center))
            SearchState.Loading -> Loading(Modifier.align(Alignment.Center))
            is SearchState.Failed -> CenterMessage(
                stringResource(if (state.error == SearchError.Network) R.string.media_error_network else R.string.media_error_other),
                null,
                Modifier.align(Alignment.Center),
            ) {
                AlarmButton(stringResource(R.string.media_retry), { onSearch(query) }, style = ButtonStyle.Text)
            }

            is SearchState.Results -> if (state.items.isEmpty()) {
                CenterMessage(
                    stringResource(R.string.media_empty_title),
                    stringResource(R.string.media_empty_body),
                    Modifier.align(Alignment.Center),
                )
            } else {
                val listState = rememberLazyListState()
                LoadMoreEffect(listState, onLoadMore)
                LazyColumn(Modifier.fillMaxSize().testTag(listTag), state = listState) {
                    items(state.items) { row(it) }
                    if (state.isLoadingMore) item { Loading(Modifier.fillMaxWidth().padding(Space.s16)) }
                }
            }
        }
    }
}

@Composable
private fun RingtoneTab(state: RingtoneState, onClick: (AlarmMedia) -> Unit) {
    ResultsBox {
        when (state) {
            RingtoneState.Loading -> Loading(Modifier.align(Alignment.Center))
            RingtoneState.Failed -> CenterMessage(
                stringResource(R.string.media_ringtone_failed),
                null,
                Modifier.align(Alignment.Center),
            )

            is RingtoneState.Loaded -> LazyColumn(Modifier.fillMaxSize().testTag("rv_ringtone")) {
                items(state.items, key = { it.id }) { RingtoneRow(it.title) { onClick(it) } }
            }
        }
    }
}

@Composable
private fun ResultsBox(content: @Composable BoxScope.() -> Unit) {
    Column {
        HorizontalDivider(color = AlarmTheme.colors.line)
        Box(Modifier.fillMaxSize(), content = content)
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.size(32.dp), color = AlarmTheme.colors.accent, strokeWidth = 3.dp)
    }
}

/** 마지막 3개 안쪽까지 내리면 다음 쪽 */
@Composable
private fun LoadMoreEffect(listState: LazyListState, onLoadMore: () -> Unit) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            (info.visibleItemsInfo.lastOrNull()?.index ?: 0) >= info.totalItemsCount - 3
        }.distinctUntilChanged().filter { it }.collect { currentOnLoadMore() }
    }
}

/** v2/SearchField. 검색어가 있으면 지우기 아이콘, 비었으면 안내 문구(dim) */
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AlarmTheme.colors
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .testTag("et_search"),
        singleLine = true,
        textStyle = AlarmTheme.typography.bodyRegular.copy(color = colors.text),
        cursorBrush = SolidColor(colors.accent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            onSearch()
        }),
        decorationBox = { field ->
            Row(
                Modifier
                    .height(48.dp)
                    .background(colors.surface, RoundedCornerShape(Radius.md))
                    .border(1.dp, colors.line, RoundedCornerShape(Radius.md))
                    .padding(start = Space.s16, end = Space.s4),
                horizontalArrangement = Arrangement.spacedBy(Space.s12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(DesignSystemR.drawable.designsystem_ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = colors.sub,
                )
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(placeholder, style = AlarmTheme.typography.bodyRegular, color = colors.dim, maxLines = 1)
                    }
                    field()
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }, Modifier.size(40.dp)) {
                        Icon(
                            painterResource(DesignSystemR.drawable.designsystem_ic_clear),
                            contentDescription = stringResource(R.string.media_clear),
                            modifier = Modifier.size(20.dp),
                            tint = colors.dim,
                        )
                    }
                }
            }
        },
    )
}
