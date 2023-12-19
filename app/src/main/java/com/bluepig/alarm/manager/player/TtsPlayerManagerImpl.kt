package com.bluepig.alarm.manager.player

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TtsPlayerManagerImpl(
    private val _context: Context
) : UtteranceProgressListener(), TtsPlayerManager, TextToSpeech.OnInitListener {

    private var _tts: TextToSpeech? = null
    private var _onSpeakDoneListener: (() -> Unit)? = null
    private var _lastMemo: String? = null

    private val tts
        get() = _tts ?: TextToSpeech(_context, this).also { _tts = it }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _tts?.language = Locale.getDefault()
            _lastMemo?.let(::speak)
        } else {
            release()
        }
    }

    private fun speak(speakText: String) = tts.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, "")

    override fun onStart(utteranceId: String?) {
        _lastMemo = null
    }

    override fun onDone(utteranceId: String?) {
        _onSpeakDoneListener?.invoke()
        _onSpeakDoneListener = null
        _lastMemo = null
    }

    override fun onError(utteranceId: String?, errorCode: Int) {
        super.onError(utteranceId, errorCode)
        _onSpeakDoneListener?.invoke()
        release()
    }

    @Deprecated("")
    override fun onError(utteranceId: String?) {
        _onSpeakDoneListener?.invoke()
        release()
    }

    override fun play(memo: String, doneListener: (() -> Unit)?) {
        doneListener?.let {
            _onSpeakDoneListener = it
            tts.setOnUtteranceProgressListener(this)
        }
        speak(memo).let { result ->
            if (result == TextToSpeech.ERROR) _lastMemo = memo
        }
    }

    override fun release() {
        _tts?.apply {
            stop()
            shutdown()
            _onSpeakDoneListener?.let { setOnUtteranceProgressListener(null) }
        }
        _onSpeakDoneListener = null
        _tts = null
    }
}