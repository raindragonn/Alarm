package com.bluepig.alarm.manager.player

import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.bluepig.alarm.domain.entity.file.SongFile

interface SongPlayerManager {
    fun init(
        lifecycle: Lifecycle,
        stateChangeListener: (isPlaying: Boolean) -> Unit,
        errorListener: (Throwable?) -> Unit
    )

    fun playSong(songFile: SongFile)
    fun playDefaultAlarm()
    fun getPlayer(): ExoPlayer
    fun getMediaItem(): MediaItem
    fun isLoading(): Boolean
    fun playEndPause()
    fun release()
}