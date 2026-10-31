package com.bluepig.alarm.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.PowerManager
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.usecase.GetAlarmById
import com.bluepig.alarm.domain.usecase.SaveAlarm
import com.bluepig.alarm.manager.player.MusicPlayerManager
import com.bluepig.alarm.manager.player.TtsPlayerManager
import com.bluepig.alarm.notification.NotificationType
import com.bluepig.alarm.ui.alarm.AlarmActivity
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

/**
 * 알람 소리·진동·TTS를 내는 포그라운드 서비스 (C1, 기술 0026).
 * 화면 상태와 상관없이 울리고, 울리기 시작할 때 다음 회차를 저장해 둔다 (C2)
 */
@AndroidEntryPoint
class AlarmRingService : LifecycleService() {
    @Inject
    lateinit var getAlarmById: GetAlarmById

    @Inject
    lateinit var saveAlarm: SaveAlarm

    @Inject
    lateinit var playerManager: MusicPlayerManager

    @Inject
    lateinit var ttsPlayerManager: TtsPlayerManager

    @Inject
    lateinit var audioManager: AudioManager

    private var _ringJob: Job? = null
    private var _firedAlarm: Alarm? = null
    private var _notificationId: Int? = null
    private var _tone: ToneGenerator? = null
    private var _playingDefault = false
    private var _originalVolume: Int? = null
    private var _wakeLock: PowerManager.WakeLock? = null

    private val _vibrator: Vibrator
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }

    override fun onCreate() {
        super.onCreate()
        // 재생 오류(파일 없음·네트워크·Direct Boot 중 외부 저장소)는 기본 알람음으로 (C5)
        // 기본 알람음까지 실패하면(잠금 해제 전엔 미디어 저장소가 없음, C7) 파일이 필요 없는 톤
        playerManager.init(
            lifecycle,
            stateChangeListener = {},
            errorListener = { if (_playingDefault) playTone() else playDefault() },
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_RING -> {
                val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)
                // 울리는 중에 다른 알람이 오면 새 알림으로 올려야 전체 화면이 다시 뜸
                startInForeground(RING_NOTIFICATION_ID + alarmId.toInt(), ringNotification(alarmId))
                ring(alarmId)
            }

            ACTION_PREVIEW -> {
                val alarm = IntentCompat.getSerializableExtra(intent, EXTRA_ALARM, Alarm::class.java)
                startInForeground(PREVIEW_NOTIFICATION_ID, previewNotification())
                if (alarm == null) stop() else start(alarm, AlarmRinging.PREVIEW_ID)
            }

            // 유튜브 영상이 울림 화면에서 재생되면 기본 알람음을 멈추고, 실패하면 다시 (C5)
            ACTION_VIDEO_PLAYING -> {
                playerManager.pause()
                _playingDefault = false
                _tone?.release()
                _tone = null
            }
            ACTION_VIDEO_FAILED -> playDefault()
            else -> stop()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    private fun ring(alarmId: Long) {
        _ringJob?.cancel()
        _ringJob = lifecycleScope.launch {
            val alarm = getAlarmById(alarmId).getOrNull() ?: return@launch stop()
            // 화면을 어떻게 벗어나든 다음 회차가 예약돼 있게 먼저 저장 (C2)
            saveAlarm(alarm.getActiveCheckedAlarm())
            BpLogger.logAlarmFired(alarm)
            _firedAlarm = alarm
            start(alarm, alarmId)
        }
    }

    private fun start(alarm: Alarm, ringingId: Long) {
        AlarmRinging.set(ringingId)
        acquireWakeLock()
        if (_originalVolume == null) _originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        setVolume(alarm.volume)
        if (alarm.memoTtsEnabled && alarm.memo.isNotBlank()) {
            ttsPlayerManager.play(alarm.memo) { playSound(alarm) }
        } else {
            playSound(alarm)
        }
    }

    private fun playSound(alarm: Alarm) {
        // 유튜브는 화면이 영상을 틀기 전까지 기본 알람음.
        // 받아 둔 노래의 캐시 DB는 credential storage라 잠금 해제 전엔 열리지 않음 (C7)
        if (alarm.media is TubeMedia) {
            playDefault()
        } else {
            runCatching { playerManager.play(alarm.media) }.onFailure { playDefault() }
        }
        if (alarm.hasVibration) vibrate()
        if (alarm.isVolumeAutoIncrease) {
            lifecycleScope.launch {
                for (step in 1..VOLUME_STEPS) {
                    setVolume(alarm.volume * step / VOLUME_STEPS)
                    delay(1_000)
                }
            }
        }
    }

    private fun playDefault() {
        if (_playingDefault) return
        _playingDefault = true
        playerManager.playDefaultAlarm()
    }

    private fun playTone() {
        if (_tone != null) return
        _tone = runCatching {
            ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME).apply {
                startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK)
            }
        }.getOrNull()
    }

    private fun setVolume(volume: Int) = audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

    private fun vibrate() {
        val effect = VibrationEffect.createWaveform(VIBRATION_PATTERN, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            _vibrator.vibrate(effect, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM))
        } else {
            @Suppress("DEPRECATION")
            _vibrator.vibrate(effect, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
        }
    }

    private fun stop() {
        _firedAlarm?.let(BpLogger::logFiredAlarmClose)
        _firedAlarm = null
        release()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        _notificationId = null
        stopSelf()
    }

    private fun release() {
        _ringJob?.cancel()
        ttsPlayerManager.release()
        playerManager.pause()
        _playingDefault = false
        _tone?.release()
        _tone = null
        _vibrator.cancel()
        _originalVolume?.let(::setVolume)
        _originalVolume = null
        _wakeLock?.takeIf { it.isHeld }?.release()
        _wakeLock = null
        AlarmRinging.set(null)
    }

    private fun acquireWakeLock() {
        if (_wakeLock != null) return
        _wakeLock = getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MediaAlarm:ring")
            .apply { acquire(WAKE_LOCK_TIMEOUT) }
    }

    private fun startInForeground(id: Int, notification: Notification) {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
        } else {
            0
        }
        ServiceCompat.startForeground(this, id, notification, type)
        _notificationId?.takeIf { it != id }?.let { NotificationManagerCompat.from(this).cancel(it) }
        _notificationId = id
    }

    private fun ringNotification(alarmId: Long): Notification {
        val screen = PendingIntent.getActivity(
            this,
            alarmId.toInt(),
            AlarmActivity.getOpenIntent(this, alarmId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
        return NotificationCompat.Builder(this, NotificationType.ALARM_RING.channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(getString(R.string.notification_alarm_title, time))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            // 다른 알림과 자동으로 묶이면 경고(전체 화면)가 생략될 수 있어 따로 둠
            .setGroup(NotificationType.ALARM_RING.channelId)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(screen)
            .setFullScreenIntent(screen, true)
            .addAction(0, getString(R.string.notification_alarm_dismiss), stopIntent(this))
            .build()
    }

    private fun previewNotification(): Notification =
        NotificationCompat.Builder(this, NotificationType.ALARM_PREVIEW.channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(getString(R.string.notification_preview_title))
            .setOngoing(true)
            .addAction(0, getString(R.string.notification_alarm_dismiss), stopIntent(this))
            .build()

    companion object {
        private const val ACTION_RING = "com.bluepig.alarm.action.RING"
        private const val ACTION_PREVIEW = "com.bluepig.alarm.action.PREVIEW"
        private const val ACTION_STOP = "com.bluepig.alarm.action.STOP"
        private const val ACTION_VIDEO_PLAYING = "com.bluepig.alarm.action.VIDEO_PLAYING"
        private const val ACTION_VIDEO_FAILED = "com.bluepig.alarm.action.VIDEO_FAILED"
        private const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
        private const val EXTRA_ALARM = "EXTRA_ALARM"
        private const val RING_NOTIFICATION_ID = 1_000_000
        private const val PREVIEW_NOTIFICATION_ID = 999_999
        private const val VOLUME_STEPS = 15
        private const val WAKE_LOCK_TIMEOUT = 30 * 60 * 1000L
        private val VIBRATION_PATTERN = longArrayOf(500, 500)

        /** 정확한 알람 브로드캐스트에서 부름 (백그라운드 포그라운드 서비스 시작 예외) */
        fun ring(context: Context, alarmId: Long) = ContextCompat.startForegroundService(
            context,
            Intent(context, AlarmRingService::class.java).setAction(ACTION_RING).putExtra(EXTRA_ALARM_ID, alarmId),
        )

        fun preview(context: Context, alarm: Alarm) = ContextCompat.startForegroundService(
            context,
            Intent(context, AlarmRingService::class.java).setAction(ACTION_PREVIEW).putExtra(EXTRA_ALARM, alarm),
        )

        fun stop(context: Context) = context.startService(command(context, ACTION_STOP))

        fun videoPlaying(context: Context) = context.startService(command(context, ACTION_VIDEO_PLAYING))

        fun videoFailed(context: Context) = context.startService(command(context, ACTION_VIDEO_FAILED))

        private fun command(context: Context, action: String) =
            Intent(context, AlarmRingService::class.java).setAction(action)

        private fun stopIntent(context: Context) = PendingIntent.getService(
            context,
            0,
            command(context, ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
