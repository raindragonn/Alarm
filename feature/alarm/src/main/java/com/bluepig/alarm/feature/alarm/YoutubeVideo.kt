package com.bluepig.alarm.feature.alarm

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bluepig.alarm.designsystem.theme.Radius
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/** 유튜브 알람 영상. 끝나면 처음부터 다시, 실패하면 [onFailed] (서비스가 기본 알람음, C5) */
@Composable
internal fun YoutubeVideo(videoId: String, onPlaying: () -> Unit, onFailed: () -> Unit, modifier: Modifier = Modifier) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnPlaying by rememberUpdatedState(onPlaying)
    val currentOnFailed by rememberUpdatedState(onFailed)
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
                lifecycle.addObserver(this)
                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) = youTubePlayer.loadVideo(videoId, 0f)

                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                        when (state) {
                            PlayerConstants.PlayerState.PLAYING -> currentOnPlaying()
                            PlayerConstants.PlayerState.ENDED -> youTubePlayer.seekTo(0f)
                            else -> Unit
                        }
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) =
                        currentOnFailed()
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
