package com.bluepig.alarm.ui.alarm

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.ActivityAlarmBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.manager.player.MusicPlayerManager
import com.bluepig.alarm.manager.player.TtsPlayerManager
import com.bluepig.alarm.util.ads.AdsManager
import com.bluepig.alarm.util.ext.audioManager
import com.bluepig.alarm.util.ext.isConnectedToInternet
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.vibrator
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {
	private val _binding: ActivityAlarmBinding by viewBinding(ActivityAlarmBinding::inflate)
	private val _vm: AlarmViewModel by viewModels()
	private val _currentDateFormat by lazy { SimpleDateFormat("MMM dd일 EE", Locale.getDefault()) }
	private val _currentTimeFormat by lazy { SimpleDateFormat("hh:mm", Locale.getDefault()) }

	@Inject
	lateinit var playerManager: MusicPlayerManager

	@Inject
	lateinit var ttsPlayerManager: TtsPlayerManager

	@Inject
	lateinit var adsManager: AdsManager

	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

		showOverLockscreen()
		disableBackButton()

		setContentView(_binding.root)
		_vm.setDefaultVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
		initPlayerManager()
		observing()
		adsManager.loadBanner(lifecycle, _binding.adFrame)
	}

	override fun onResume() {
		super.onResume()
		BpLogger.logScreenView(AlarmActivity::class.java.simpleName)
	}

	override fun onDestroy() {
		release()
		super.onDestroy()
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
								startVibration(alarm)
								setVolume(alarm)
								setTts(alarm)
								if (!_vm.isPreview) BpLogger.logAlarmFired(alarm)
							}.onFailure { e ->
								showErrorToast(e)
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

	private fun release() {
		_binding.yp.release()
		playerManager.release()
		ttsPlayerManager.release()
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, _vm.getDefaultVolume(), 0)
		vibrator.cancel()
		_vm.updateAlarmExpired()
	}

	private fun bindViews(alarm: Alarm) = with(_binding) {
		alarm.media
			.onMusic {
				ivThumbnail.setThumbnail(it.thumbnail)
			}
			.onRingtone {
				setDefaultThumbnail()
			}
			.onTube {
				ivThumbnail.isVisible = false
				yp.isVisible = true
			}
		tvMemo.text = alarm.memo

		if (_vm.isPreview) {
			previewSetting()
		} else {
			alarmSetting()
		}
	}

	private fun setDefaultThumbnail() {
		val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_alarm, null) ?: return
		_binding.ivThumbnail.isVisible = true
		_binding.ivThumbnail.setThumbnail(drawable)
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
	private fun startVibration(alarm: Alarm) = lifecycleScope.launch {
		if (alarm.memoTtsEnabled) return@launch
		if (!alarm.hasVibration) return@launch
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, 0))
		} else {
			vibrator.vibrate(VIBRATION_PATTERN, 0)
		}
	}

	private fun setTts(alarm: Alarm) {
		if (!alarm.memoTtsEnabled) return
		// start Tts
		ttsPlayerManager.play(alarm.memo) {
			val ttsOverAlarm = alarm.copy(memoTtsEnabled = false)
			startAlarmMedia(ttsOverAlarm)
			setVolume(ttsOverAlarm)
			startVibration(ttsOverAlarm)
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

	private fun initPlayerManager() {
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
		val media = alarm.media
		if (media !is TubeMedia) {
			playerManager.play(alarm.media)
		} else {
			initYoutubePlayer(_binding.yp, media)
			if (!applicationContext.isConnectedToInternet) {
				setDefaultThumbnail()
				playerManager.playDefaultAlarm()
			}
		}
	}

	private fun initYoutubePlayer(yp: YouTubePlayerView, tubeMedia: TubeMedia) {
		lifecycle.addObserver(yp)
		val listener = object : AbstractYouTubePlayerListener() {
			override fun onReady(youTubePlayer: YouTubePlayer) {
				_binding.ivThumbnail.isVisible = false
				playerManager.release()
				val controller = DefaultPlayerUiController(yp, youTubePlayer).apply {
					showFullscreenButton(false)
					showYouTubeButton(false)
				}
				yp.setCustomPlayerUi(controller.rootView)
				youTubePlayer.loadOrCueVideo(
					lifecycle,
					tubeMedia.videoId,
					0f
				)
			}

			override fun onStateChange(
				youTubePlayer: YouTubePlayer,
				state: PlayerConstants.PlayerState
			) {
				super.onStateChange(youTubePlayer, state)
				if (state == PlayerConstants.PlayerState.ENDED) {
					youTubePlayer.seekTo(0F)
				}
			}

			override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
				super.onError(youTubePlayer, error)
				BpLogger.logException(Exception("$error"))
				setDefaultThumbnail()
				playerManager.playDefaultAlarm()
			}
		}
		val options = IFramePlayerOptions.Builder().controls(0).build()
		yp.initialize(listener, true, options)
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
