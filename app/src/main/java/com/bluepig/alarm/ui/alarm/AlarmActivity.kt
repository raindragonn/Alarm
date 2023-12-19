package com.bluepig.alarm.ui.alarm

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.ActivityAlarmBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.result.onFailureWitLoading
import com.bluepig.alarm.manager.player.MusicPlayerManager
import com.bluepig.alarm.manager.player.TtsPlayerManager
import com.bluepig.alarm.util.ext.audioManager
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.vibrator
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {
    private val _binding: ActivityAlarmBinding by viewBinding(ActivityAlarmBinding::inflate)
    private val _vm: AlarmViewModel by viewModels()
    private val _currentDateFormat by lazy { SimpleDateFormat("MMM dd일 EE", Locale.getDefault()) }
    private val _currentTimeFormat by lazy { SimpleDateFormat("hh:mm", Locale.getDefault()) }

    private var _defaultVolume: Int? = null

    @Inject
    lateinit var playerManager: MusicPlayerManager

    @Inject
    lateinit var ttsPlayerManager: TtsPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        _defaultVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        initAlarmMedia()

        showOverLockscreen()
        disableBackButton()
        observing()
    }

    private fun observing() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    _vm.getAlarmState()
                        .stateIn(this)
                        .collect {
                            it.onSuccess { alarm ->
                                bindViews(alarm)
                                startAlarmMedia(alarm)
                                setVibration(alarm)
                                setVolume(alarm)
                                setTts(alarm)
                            }.onFailureWitLoading { e ->
                                Timber.w(e)
                                finishAffinity()
                            }
                        }
                }
                launch {
                    _vm.volume
                        .stateIn(this)
                        .collect { volume ->
                            changeVolume(volume)
                        }
                }
                launch {
                    _vm.getDateTime()
                        .stateIn(this)
                        .collect(::bindCurrentTime)
                }
            }
        }
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    private fun release() {
        playerManager.release()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _defaultVolume ?: 7, 0)
        vibrator.cancel()
        _vm.updateAlarmExpired()
    }

    private fun bindViews(alarm: Alarm) = with(_binding) {
        alarm.media
            .onMusic {
                ivThumbnail.setThumbnail(it.thumbnail)
            }
            .onRingtone {
                val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_alarm, null)
                ivThumbnail.setThumbnail(drawable ?: return@onRingtone)
            }
        tvMemo.text = alarm.memo

        if (_vm.isPreview) {
            previewSetting()
        } else {
            alarmSetting()
        }
    }

    private fun bindCurrentTime(dateTimeInMillis: Long) {
        val date = _currentDateFormat.format(dateTimeInMillis)
        val time = _currentTimeFormat.format(dateTimeInMillis)

        _binding.tvDate.text = date
        _binding.tvTime.text = time
    }

    private fun previewSetting() {
        _binding.btnAlarmEnd.text = getString(R.string.alarm_preview_end)
        _binding.btnAlarmEnd.setOnClickListener {
            finish()
        }
    }

    private fun alarmSetting() {
        _binding.btnAlarmEnd.text = getString(R.string.alarm_end)
        _binding.btnAlarmEnd.setOnClickListener {
            _vm.updateAlarmExpired()
            finish()
        }
    }

    private fun changeVolume(volume: Int) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            0
        )
    }

    @Suppress("DEPRECATION")
    private fun setVibration(alarm: Alarm) = lifecycleScope.launch {
        if (alarm.memoTtsEnabled) return@launch
        if (alarm.hasVibration.not()) return@launch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, 0))
        } else {
            vibrator.vibrate(VIBRATION_PATTERN, 0)
        }
    }

    private fun setTts(alarm: Alarm) {
        if (alarm.memoTtsEnabled.not()) return
        // start Tts
        ttsPlayerManager.play(alarm.memo) {
            val ttsOverAlarm = alarm.copy(memoTtsEnabled = false)
            startAlarmMedia(ttsOverAlarm)
            setVolume(ttsOverAlarm)
            setVibration(ttsOverAlarm)
        }
    }

    private fun setVolume(alarm: Alarm) = lifecycleScope.launch {
        if (alarm.memoTtsEnabled) {
            _vm.setAlarmVolume(alarm.volume)
            return@launch
        }

        if (alarm.isVolumeAutoIncrease) {
            _vm.startAutoIncreaseVolume(alarm.volume)
        } else {
            _vm.setAlarmVolume(alarm.volume)
        }
    }

    private fun disableBackButton() {
        onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // 백버튼으로 닫기 불가
                    // TODO: 백버튼 시 소리끄기, 진동 끄기 등
                }
            })
    }

    @Suppress("DEPRECATION")
    private fun showOverLockscreen() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }

    private fun initAlarmMedia() {
        playerManager.init(
            lifecycle,
            stateChangeListener = ::onPlayingStateChanged,
            errorListener = {
                showErrorToast(it) {
                    playerManager.playDefaultAlarm()
                }
            })

    }

    private fun startAlarmMedia(alarm: Alarm) = lifecycleScope.launch {
        if (alarm.memoTtsEnabled) return@launch
        playerManager.play(alarm.media)
    }

    private fun onPlayingStateChanged(isPlaying: Boolean) {}

    companion object {
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
        const val EXTRA_PREVIEW_ALARM = "EXTRA_IS_PREVIEW"

        private val VIBRATION_PATTERN = longArrayOf(500, 500)

        fun getOpenIntent(context: Context, alarmId: Long): Intent {
            return Intent(context, AlarmActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
        }


        fun openPreView(context: Context, alarm: Alarm) {
            val intent = Intent(context, AlarmActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_PREVIEW_ALARM, alarm)
            }
            context.startActivity(intent)
        }
    }
}