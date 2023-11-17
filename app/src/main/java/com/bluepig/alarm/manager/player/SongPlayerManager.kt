package com.bluepig.alarm.manager.player

import androidx.lifecycle.Lifecycle
import androidx.media3.exoplayer.ExoPlayer

interface SongPlayerManager {
    fun init(lifecycle: Lifecycle, callBack: (isPlaying: Boolean) -> Unit)
    fun setSongUrl(songUrl: String)
    fun getPlayer(): ExoPlayer
    fun isLoading(): Boolean
    fun playEndPause()
    fun release()
}