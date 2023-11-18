package com.bluepig.alarm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService
import com.bluepig.alarm.databinding.ActivityMainBinding
import com.bluepig.alarm.service.MediaDownloadService
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
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