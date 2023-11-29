package com.bluepig.alarm.notification

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.R
import kotlinx.coroutines.launch

object NotificationHelper {

    fun checkNotificationPermission(activity: ComponentActivity) {
        activity.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if (isGranted) {
                        NotificationType.DOWNLOAD_NOTIFICATION.createChannel(activity)
                    } else {
                        activity.showOkButtonDialog(
                            R.string.alert_permission_title_post_notification,
                            R.string.alert_permission_text_post_notification,
                        ) {
                            openAppNotificationSettings(activity)
                        }
                    }
                }


            activity.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    when {
                        ContextCompat.checkSelfPermission(
                            this@apply,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            NotificationType.DOWNLOAD_NOTIFICATION.createChannel(activity)
                        }

                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this@apply, Manifest.permission.POST_NOTIFICATIONS
                        ) -> {
                            activity.showOkButtonDialog(
                                R.string.alert_permission_title_post_notification,
                                R.string.alert_permission_text_post_notification,
                            ) {
                                openAppNotificationSettings(activity)
                            }
                        }

                        else -> {
                            requestPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    }
                }
            }
        }

    }

    private fun Context.showOkButtonDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        function: () -> Unit
    ) {
        runCatching {
            AlertDialog.Builder(this)
                .setTitle(getString(title))
                .setMessage(getString(message))
                .setPositiveButton(R.string.ok) { _, _ -> function() }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }.onFailure {
            function()
        }
    }

    private fun openAppNotificationSettings(context: Context) {
        val intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            }
        context.startActivity(intent)
    }
}