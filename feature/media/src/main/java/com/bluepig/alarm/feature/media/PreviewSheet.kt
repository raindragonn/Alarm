package com.bluepig.alarm.feature.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bluepig.alarm.designsystem.component.AlarmButton
import com.bluepig.alarm.designsystem.component.ButtonStyle
import com.bluepig.alarm.designsystem.component.MediaTile
import com.bluepig.alarm.designsystem.component.MediaTileType
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.designsystem.theme.Radius
import com.bluepig.alarm.designsystem.theme.Space
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import com.bluepig.alarm.designsystem.R as DesignSystemR

/** 미리듣기 바텀시트. 노래·벨소리는 Media3, 유튜브는 YouTube 플레이어(디자인에 없어 16:9 영상으로 맞춤) */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun PreviewSheet(preview: PreviewState, onDismiss: () -> Unit, onSelect: () -> Unit) {
    val colors = AlarmTheme.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = Radius.xl, topEnd = Radius.xl),
        containerColor = colors.surface,
        dragHandle = {
            Box(
                Modifier
                    .padding(top = Space.s8, bottom = Space.s4)
                    .size(32.dp, 4.dp)
                    .background(colors.dim, RoundedCornerShape(2.dp)),
            )
        },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = Space.s20, end = Space.s20, bottom = Space.s16)
                .semantics { testTagsAsResourceId = true },
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss, Modifier.size(40.dp)) {
                    Icon(
                        painterResource(DesignSystemR.drawable.designsystem_ic_close),
                        contentDescription = stringResource(R.string.media_close),
                        modifier = Modifier.size(22.dp),
                        tint = colors.text,
                    )
                }
            }
            when (preview) {
                is PreviewState.Loading -> {
                    MediaHeader(preview.title, MediaTileType.Song, null, R.string.media_type_song)
                    Box(Modifier.fillMaxWidth().height(116.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.accent)
                    }
                }

                is PreviewState.Ready -> when (val media = preview.media) {
                    is TubeMedia -> YoutubePreview(media, onSelect)
                    else -> AudioPreview(media, onSelect)
                }
            }
            Spacer(Modifier.height(Space.s24))
        }
    }
}

@Composable
private fun MediaHeader(title: String, type: MediaTileType, thumbnail: String?, caption: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(Space.s12), verticalAlignment = Alignment.CenterVertically) {
        MediaTile(type, thumbnail = thumbnail, size = 56.dp, cornerRadius = 14.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                title,
                style = AlarmTheme.typography.titleXsmall,
                color = AlarmTheme.colors.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(stringResource(caption), style = AlarmTheme.typography.captionRegular, color = AlarmTheme.colors.sub)
        }
    }
}

@Composable
private fun AudioPreview(media: AlarmMedia, onSelect: () -> Unit) {
    val (type, thumbnail, caption, uri) = when (media) {
        is MusicMedia -> Quad(MediaTileType.Song, media.thumbnail, R.string.media_type_song, media.fileUrl)
        is RingtoneMedia -> Quad(MediaTileType.Ringtone, null, R.string.media_type_ringtone, media.uri)
        is TubeMedia -> error("유튜브는 YoutubePreview")
    }
    val player = rememberPreviewPlayer()
    var isPlaying by remember { mutableStateOf(false) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    LaunchedEffect(player, uri) {
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
    }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }
    LaunchedEffect(player) {
        while (true) {
            position = player.currentPosition
            duration = player.duration.coerceAtLeast(0L)
            delay(250)
        }
    }

    MediaHeader(media.title, type, thumbnail, caption)
    Column(Modifier.padding(top = Space.s20), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val colors = AlarmTheme.colors
        val fraction = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f
        Box(Modifier.fillMaxWidth().height(4.dp).background(colors.fill, RoundedCornerShape(2.dp))) {
            Box(Modifier.fillMaxWidth(fraction).height(4.dp).background(colors.accent, RoundedCornerShape(2.dp)))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(clock(position), style = AlarmTheme.typography.numCaption, color = colors.sub)
            Text(clock(duration), style = AlarmTheme.typography.numCaption, color = colors.sub)
        }
    }
    PreviewActions(isPlaying, onToggle = { if (player.isPlaying) player.pause() else player.play() }, onSelect)
}

@Composable
private fun YoutubePreview(media: TubeMedia, onSelect: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var youtubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
                lifecycle.addObserver(this)
                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youtubePlayer = youTubePlayer
                        youTubePlayer.loadVideo(media.videoId, 0f)
                    }

                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                        isPlaying = state == PlayerConstants.PlayerState.PLAYING
                    }
                }
                initialize(listener, IFramePlayerOptions.Builder(context).controls(0).build())
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(Radius.md)),
        onRelease = {
            lifecycle.removeObserver(it)
            it.release()
        },
    )
    Text(
        media.title,
        Modifier.padding(top = Space.s12),
        style = AlarmTheme.typography.titleXsmall,
        color = AlarmTheme.colors.text,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
    Text(stringResource(R.string.media_type_youtube), style = AlarmTheme.typography.captionRegular, color = AlarmTheme.colors.sub)
    PreviewActions(
        isPlaying,
        onToggle = { youtubePlayer?.let { if (isPlaying) it.pause() else it.play() } },
        onSelect,
    )
}

@Composable
private fun PreviewActions(isPlaying: Boolean, onToggle: () -> Unit, onSelect: () -> Unit) {
    Row(Modifier.padding(top = Space.s20), horizontalArrangement = Arrangement.spacedBy(Space.s8)) {
        AlarmButton(
            text = stringResource(if (isPlaying) R.string.media_pause else R.string.media_play),
            onClick = onToggle,
            modifier = Modifier.weight(1f),
            style = ButtonStyle.Secondary,
            icon = if (isPlaying) DesignSystemR.drawable.designsystem_ic_pause else DesignSystemR.drawable.designsystem_ic_play,
        )
        AlarmButton(stringResource(R.string.media_select), onClick = onSelect, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun rememberPreviewPlayer(): ExoPlayer {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply { repeatMode = Player.REPEAT_MODE_ONE }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    // 화면을 벗어나면 멈춤
    LifecycleStartEffect(player) { onStopOrDispose { player.pause() } }
    return player
}

private fun clock(millis: Long): String {
    val seconds = millis / 1000
    return "%d:%02d".format(seconds / 60, seconds % 60)
}

private data class Quad(val type: MediaTileType, val thumbnail: String?, val caption: Int, val uri: String)
