package com.bluepig.alarm.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService
import com.bluepig.alarm.databinding.ActivityMainBinding
import com.bluepig.alarm.notification.NotificationType
import com.bluepig.alarm.service.MediaDownloadService
import com.bluepig.alarm.util.PermissionHelper
import com.bluepig.alarm.util.ads.AdsManager
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
	private val _binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
	private val _vm: MainViewModel by viewModels()

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
		setContentView(_binding.root)
		PermissionHelper.checkNotificationPermission(this) {
			NotificationType.DOWNLOAD_NOTIFICATION.createChannel(this)
		}
		PermissionHelper.checkExactAlarmPermission(this, _binding.root) {
			_vm.refresh()
		}
		PermissionHelper.checkSystemAlertPermission(this, _binding.root)
		adsManager.loadBottomNativeAd(lifecycle, _binding.adFrame)
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.CREATED) {
				startDownloadService()
			}
		}
	}

	override fun onResume() {
		super.onResume()
		BpLogger.logScreenView(MainActivity::class.java.simpleName)
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
			BpLogger.logException(it)
		}
		_vm.startDownload()
	}
}
