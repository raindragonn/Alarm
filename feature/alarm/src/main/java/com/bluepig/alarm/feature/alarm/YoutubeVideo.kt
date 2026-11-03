package com.bluepig.alarm.feature.alarm

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bluepig.alarm.designsystem.theme.Radius
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay

/**
 * 유튜브 알람 영상. [play]가 true가 되면 재생하고 끝나면 처음부터 다시.
 * 화면을 벗어나거나(플레이어가 멈춤) 실패하면 [onStopped] (서비스가 기본 알람음, C5).
 * 화면에서 직접 멈추면 조용히 두고 1분 뒤 다시 재생 (제품 0018)
 */
@Composable
internal fun YoutubeVideo(
    videoId: String,
    play: Boolean,
    onPlaying: () -> Unit,
    onStopped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnPlaying by rememberUpdatedState(onPlaying)
    val currentOnStopped by rememberUpdatedState(onStopped)
    val currentPlay by rememberUpdatedState(play)
    var player by remember { mutableStateOf<YouTubePlayer?>(null) }
    var pausedOnScreen by remember { mutableStateOf(false) }
    // 메모를 다 읽거나 화면으로 돌아오면 재생 (화면을 벗어나면 플레이어가 멈추고 이어 재생하지 않음).
    // 준비 전이면 onReady가 재생. 준비 직후 cue에 이어 부른 재생은 무시돼 준비 때 한 번만 부름
    LifecycleResumeEffect(play) {
        if (play) player?.play()
        onPauseOrDispose {}
    }
    // 메모를 읽는 중에 벗어나면 서비스가 다 읽은 뒤 기본음을 냄
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        pausedOnScreen = false
        if (currentPlay) currentOnStopped()
    }
    LaunchedEffect(pausedOnScreen) {
        if (!pausedOnScreen) return@LaunchedEffect
        delay(RESUME_AFTER_PAUSE)
        player?.play()
    }
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
                lifecycle.addObserver(this)
                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        player = youTubePlayer
                        if (currentPlay) youTubePlayer.loadVideo(videoId, 0f) else youTubePlayer.cueVideo(videoId, 0f)
                    }

                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                        // 화면을 벗어나며 멈춘 건 ON_STOP이 처리
                        val onScreen = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
                        when (state) {
                            PlayerConstants.PlayerState.PLAYING -> {
                                pausedOnScreen = false
                                if (onScreen) currentOnPlaying()
                            }

                            PlayerConstants.PlayerState.PAUSED -> if (onScreen) pausedOnScreen = true
                            PlayerConstants.PlayerState.ENDED -> youTubePlayer.seekTo(0f)
                            else -> Unit
                        }
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) =
                        currentOnStopped()
                }
                initialize(listener, IFramePlayerOptions.Builder(context).controls(0).build())
            }
        },
        modifier = modifier.clip(RoundedCornerShape(Radius.md)),
        onRelease = {
            lifecycle.removeObserver(it)
            it.release()
        },
    )
}

private const val RESUME_AFTER_PAUSE = 60_000L
