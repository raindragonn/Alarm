package com.bluepig.alarm.manager.player

interface TtsPlayerManager {
    fun play(memo: String, doneListener: (() -> Unit)? = null)
    fun release()
}