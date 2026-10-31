package com.bluepig.alarm.ui.alarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.IntentCompat
import androidx.core.os.UserManagerCompat
import androidx.lifecycle.lifecycleScope
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.feature.alarm.AlarmRingRoute
import com.bluepig.alarm.feature.alarm.AlarmRingViewModel
import com.bluepig.alarm.service.AlarmRingService
import com.bluepig.alarm.service.AlarmRinging
import com.bluepig.alarm.util.ads.AdsManager
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 잠금 화면 위 울림·미리보기 화면 (기술 0022). 화면은 feature:alarm, 소리는 AlarmRingService.
 * 잠금 화면 위 표시·화면 켜기·Direct Boot는 매니페스트 속성
 */
@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
    @Inject
    lateinit var adsManager: AdsManager

    private val _previewAlarm: Alarm?
        get() = IntentCompat.getSerializableExtra(intent, AlarmRingViewModel.KEY_PREVIEW_ALARM, Alarm::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (savedInstanceState == null) _previewAlarm?.let { AlarmRingService.preview(this, it) }

        // 서비스가 멈추면(알림의 끄기 등) 화면도 닫음
        lifecycleScope.launch {
            AlarmRinging.alarmId.dropWhile { it == null }.first { it == null }
            finish()
        }

        setContent {
            AlarmTheme {
                AlarmRingRoute(
                    canPlayVideo = UserManagerCompat.isUserUnlocked(this),
                    onDismiss = ::dismiss,
                    onVideoPlaying = { AlarmRingService.videoPlaying(this) },
                    onVideoFailed = { AlarmRingService.videoFailed(this) },
                    onNotFound = ::dismiss,
                    banner = { modifier ->
                        AndroidView(
                            factory = { context ->
                                FrameLayout(context).also { adsManager.loadBanner(lifecycle, it) }
                            },
                            modifier = modifier,
                        )
                    },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(AlarmActivity::class.java.simpleName)
    }

    override fun onStop() {
        super.onStop()
        // 미리보기는 화면을 벗어나면 끝. 울리는 알람은 끌 때까지 서비스가 계속 울림 (C2)
        if (_previewAlarm != null && !isChangingConfigurations) dismiss()
    }

    private fun dismiss() {
        AlarmRingService.stop(this)
        finish()
    }

    companion object {
        fun getOpenIntent(context: Context, alarmId: Long): Intent =
            Intent(context, AlarmActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(AlarmRingViewModel.KEY_ALARM_ID, alarmId)

        fun openPreView(context: Context, alarm: Alarm) {
            context.startActivity(
                Intent(context, AlarmActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(AlarmRingViewModel.KEY_PREVIEW_ALARM, alarm),
            )
        }
    }
}
