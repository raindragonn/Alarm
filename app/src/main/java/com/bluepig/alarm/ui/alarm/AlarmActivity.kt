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
import androidx.core.view.isInvisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.ActivityAlarmBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.result.onFailureWitLoading
import com.bluepig.alarm.manager.player.SongPlayerManager
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
    lateinit var playerManager: SongPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        _vm.setAlarmState()
        _vm.startDateTime()
        _defaultVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        showOverLockscreen()
        disableBackButton()
        observing()
    }

    private fun observing() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    _vm.alarmState
                        .stateIn(this)
                        .collect {
                            it.onSuccess { alarm ->
                                initViews(alarm)
                                setUpAlarmSong(alarm)
                                setVibration(alarm)
                                _vm.startAutoIncreaseVolume(
                                    alarm.volume
                                        ?: audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                )
                            }.onFailureWitLoading { e ->
                                Timber.w(e)
                                finishAffinity()
                            }
                        }
                }
                launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        _vm.volumeIncreaseState
                            .stateIn(this)
                            .collect { volume ->
                                changeVolume(volume)
                            }
                    }
                }
                launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        _vm.currentTimeState
                            .stateIn(this)
                            .collect(::bindCurrentTime)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun release() {
        playerManager.release()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _defaultVolume ?: 7, 0)
        vibrator.cancel()
        _vm.updateAlarmExpired()
    }

    private fun initViews(alarm: Alarm) = with(_binding) {
        when (val media = alarm.media) {
            is MusicMedia -> {
                ivThumbnail.setThumbnail(media.thumbnail)
            }

            else -> {
                ivThumbnail.isInvisible = true
            }
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
    private fun setVibration(alarm: Alarm) {
        if (alarm.hasVibration.not()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, 0))
        } else {
            vibrator.vibrate(VIBRATION_PATTERN, 0)
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

    private fun setUpAlarmSong(alarm: Alarm) {
        playerManager.init(
            lifecycle,
            stateChangeListener = ::onPlayingStateChanged,
            errorListener = {
                showErrorToast(it) {
                    playerManager.playDefaultAlarm()
                }
            })

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