package com.bluepig.alarm.util

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bluepig.alarm.R
import com.bluepig.alarm.util.ext.alarmManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

object PermissionHelper {

    fun checkNotificationPermission(activity: ComponentActivity, grantedAction: () -> Unit) {
        activity.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if (isGranted) {
                        grantedAction.invoke()
                    } else {
                        activity.showOkButtonDialog(
                            R.string.alert_request_permission_title,
                            R.string.alert_request_permission_text_post_notification,
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
                            grantedAction.invoke()
                        }

                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this@apply, Manifest.permission.POST_NOTIFICATIONS
                        ) -> {
                            activity.showOkButtonDialog(
                                R.string.alert_request_permission_title,
                                R.string.alert_request_permission_text_post_notification,
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

    fun checkSystemAlertPermission(activity: ComponentActivity, anchorView: View) {
        activity.apply {
            if (Settings.canDrawOverlays(this)) return

            activity.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    if (!Settings.canDrawOverlays(activity)) {
                        Snackbar.make(
                            anchorView,
                            R.string.alert_request_permission_text_system_alert_window,
                            Snackbar.LENGTH_INDEFINITE
                        ).apply {
                            setAction(R.string.ok) {
                                openSystemAlertWindow(activity)
                            }
                        }.show()
                    }
                }
            }
        }
    }

    fun checkExactAlarmPermission(
        activity: ComponentActivity,
        anchorView: View,
        grantedAction: () -> Unit
    ) {
        activity.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                return
            }

            activity.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                        Snackbar.make(
                            anchorView,
                            R.string.alert_request_permission_text_exact_alarm,
                            Snackbar.LENGTH_INDEFINITE
                        ).apply {
                            setAction(R.string.ok) {
                                openExactAlarmSetting(activity)
                            }
                        }.show()
                    } else {
                        grantedAction.invoke()
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

    private fun openSystemAlertWindow(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }

    private fun openExactAlarmSetting(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}