package com.bluepig.alarm.manager.player

import android.content.Context
import android.media.RingtoneManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.bluepig.alarm.domain.entity.file.SongFile
import com.bluepig.alarm.domain.result.NotFoundMediaItemException
import com.bluepig.alarm.domain.result.NotFoundPlayerException
import com.bluepig.alarm.manager.download.MediaDownloadManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@UnstableApi
class SongPlayerManagerImpl @Inject constructor(
    @ApplicationContext
    private val _context: Context,
    private val _downloadManager: MediaDownloadManager,
) : SongPlayerManager {
    private var _player: ExoPlayer? = null
    private var _lifecycle: Lifecycle? = null
    private var _mediaItem: MediaItem? = null

    private var _eventObserver: EventObserver? = null
    private var _errorObserver: PlayerErrorListener? = null
    private var _playListener: Player.Listener? = null

    override fun init(
        lifecycle: Lifecycle,
        stateChangeListener: (isPlaying: Boolean) -> Unit,
        errorListener: (Throwable?) -> Unit
    ) {
        _player = ExoPlayer.Builder(_context).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE

            PlayerListener(stateChangeListener).let {
                _playListener = it
                addListener(it)
            }
            PlayerErrorListener(errorListener).let {
                _errorObserver = it
                addListener(it)
            }
        }
        EventObserver().let {
            _eventObserver = it
            lifecycle.addObserver(it)
        }
    }

    override fun playSong(songFile: SongFile) {
        val mediaItem = _downloadManager.getMediaItem(songFile)
            .also { _mediaItem = it }
        val mediaSource =
            ProgressiveMediaSource
                .Factory(_downloadManager.getDataSourceFactory())
                .createMediaSource(mediaItem)

        _player?.playWhenReady = true
        _player?.setMediaSource(mediaSource)
        _player?.prepare()
    }

    override fun playDefaultAlarm() {
        val mediaItem = MediaItem.fromUri(
            RingtoneManager.getActualDefaultRingtoneUri(
                _context,
                RingtoneManager.TYPE_ALARM
            ).toString()
        ).also { _mediaItem = it }

        val mediaSource =
            ProgressiveMediaSource
                .Factory(DefaultDataSource.Factory(_context))
                .createMediaSource(mediaItem)

        _player?.playWhenReady = true
        _player?.setMediaSource(mediaSource)
        _player?.prepare()
    }

    override fun isLoading(): Boolean = _player?.isLoading ?: throw NotFoundPlayerException

    override fun getPlayer(): ExoPlayer =
        _player ?: throw NotFoundPlayerException

    override fun getMediaItem(): MediaItem =
        _mediaItem ?: throw NotFoundMediaItemException

    override fun playEndPause() {
        _player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        } ?: return
    }

    override fun release() {
        _playListener?.let {
            _player?.removeListener(it)
        }
        _playListener = null

        _eventObserver?.let {
            _lifecycle?.removeObserver(_eventObserver!!)
        }
        _eventObserver = null

        _errorObserver?.let {
            _player?.removeListener(it)
        }
        _errorObserver = null

        _player?.release()
        _player = null
    }

    private class PlayerListener(
        private val _callBack: (isPlaying: Boolean) -> Unit
    ) : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _callBack.invoke(isPlaying)
        }
    }

    private class PlayerErrorListener(
        private val _callBack: (error: Throwable) -> Unit
    ) : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            error.cause?.let(_callBack)
        }
    }

    private inner class EventObserver : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_START -> _player?.play()
                Lifecycle.Event.ON_STOP -> _player?.pause()
                Lifecycle.Event.ON_DESTROY -> release()
                else -> {}
            }
        }
    }
}