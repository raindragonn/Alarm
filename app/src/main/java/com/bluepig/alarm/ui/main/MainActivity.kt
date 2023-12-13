package com.bluepig.alarm.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService
import com.bluepig.alarm.databinding.ActivityMainBinding
import com.bluepig.alarm.domain.result.onFailureWitLoading
import com.bluepig.alarm.notification.NotificationType
import com.bluepig.alarm.service.MediaDownloadService
import com.bluepig.alarm.util.PermissionHelper
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
    private val _vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        PermissionHelper.checkNotificationPermission(this) {
            NotificationType.DOWNLOAD_NOTIFICATION.createChannel(this)
        }
        PermissionHelper.checkSystemAlertPermission(this, _binding.root)
        PermissionHelper.checkExactAlarmPermission(this, _binding.root)
        startDownloadService()
    }

    private fun startDownloadService() {
        kotlin.runCatching {
            DownloadService.start(
                this,
                MediaDownloadService::class.java,
            )
        }.onFailureWitLoading {
            DownloadService.startForeground(
                this,
                MediaDownloadService::class.java,
            )
            Timber.e(it)
        }
        _vm.startDownload()
    }
}