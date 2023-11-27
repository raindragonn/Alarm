package com.bluepig.alarm.ui.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bluepig.alarm.databinding.ActivityAlarmBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.manager.player.SongPlayerManager
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {
    private val _binding: ActivityAlarmBinding by viewBinding(ActivityAlarmBinding::inflate)

    @Inject
    lateinit var playerManager: SongPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        showOverLockscreen()

        val alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_ALARM, Alarm::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_ALARM) as Alarm
        } ?: return
        playerManager.init(lifecycle) {

        }
        playerManager.setSongUrl(alarm.file)
        _binding.ivThumbnail.setThumbnail(alarm.file.thumbnail)
    }

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


    companion object {
        const val EXTRA_ALARM = "EXTRA_ALARM"
        private const val EXTRA_IS_PREVIEW = "EXTRA_IS_PREVIEW"

        fun openPreView(context: Context, alarm: Alarm) {
            val intent = Intent(context, AlarmActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_ALARM, alarm)
                putExtra(EXTRA_IS_PREVIEW, true)
            }
            context.startActivity(intent)
        }
    }
}