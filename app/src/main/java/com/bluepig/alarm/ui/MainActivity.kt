package com.bluepig.alarm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService
import com.bluepig.alarm.databinding.ActivityMainBinding
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.manager.download.MediaDownloadManager
import com.bluepig.alarm.notification.NotificationHelper
import com.bluepig.alarm.service.MediaDownloadService
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        NotificationHelper.checkNotificationPermission(this)
        startDownloadService()
    }

    private fun startDownloadService() {
        kotlin.runCatching {

            DownloadService.start(
                this,
                MediaDownloadService::class.java,
            )
        }.onFailure {
            DownloadService.startForeground(
                this,
                MediaDownloadService::class.java,
            )
            Timber.e(it)
        }.onSuccess {
            Timber.d("success")
        }
    }
}