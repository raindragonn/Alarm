package com.bluepig.alarm.manager.player

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
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
    private var _eventObserver: EventObserver? = null
    private var _playListener: Player.Listener? = null
    private var _mediaItem: MediaItem? = null

    override fun init(lifecycle: Lifecycle, callBack: (isPlaying: Boolean) -> Unit) {
        _player = ExoPlayer.Builder(_context).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE

            PlayerListener(callBack).let {
                _playListener = it
                addListener(it)
            }
        }
        EventObserver().let {
            _eventObserver = it
            lifecycle.addObserver(it)
        }
    }

    override fun setSongUrl(songFile: SongFile) {
        val mediaItem = _downloadManager.getMediaItem(songFile.fileUrl, songFile.id)
            .also { _mediaItem = it }
        val mediaSource =
            ProgressiveMediaSource
                .Factory(_downloadManager.getDataSourceFactory())
                .createMediaSource(mediaItem)

        _player?.playWhenReady = true
        _player?.addMediaSource(mediaSource)
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
        if (_player != null) {
            _player!!.release()
            if (_playListener != null) {
                _player!!.removeListener(_playListener!!)
                _playListener = null
            }
            _player = null
        }
    }

    private class PlayerListener(
        private val _callBack: (isPlaying: Boolean) -> Unit
    ) : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _callBack.invoke(isPlaying)
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