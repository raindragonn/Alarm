package com.bluepig.alarm.util

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.bluepig.alarm.R

object PermissionHelper {

    /**
     * 알림 권한(33+). 울림 화면은 전체 화면 알림으로 뜨므로 필요 (기술 0026).
     * onCreate에서 한 번만 물음 (U4: 화면을 다시 볼 때마다 대화상자가 반복·중첩됐음)
     */
    fun requestNotificationPermission(activity: ComponentActivity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) return
        val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) activity.showNotificationRationale()
        }
        launcher.launch(permission)
    }

    /** 정확한 알람 권한 설정 화면 (31·32만, 기술 0025) */
    fun openExactAlarmSetting(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).setData("package:${context.packageName}".toUri()),
        )
    }

    /** 다른 앱 위에 표시 권한 설정 화면 (기술 0031) */
    fun openOverlaySetting(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).setData("package:${context.packageName}".toUri()),
        )
    }

    private fun Context.showNotificationRationale() {
        AlertDialog.Builder(this)
            .setTitle(R.string.alert_request_permission_title)
            .setMessage(R.string.alert_request_permission_text_post_notification)
            .setPositiveButton(R.string.ok) { _, _ ->
                startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName),
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}

/** 다른 앱 위에 표시 권한. 설정에서 바꾸고 돌아오면 다시 읽음 */
@Composable
fun rememberCanDrawOverlays(): Boolean {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    LifecycleResumeEffect(Unit) {
        granted = Settings.canDrawOverlays(context)
        onPauseOrDispose {}
    }
    return granted
}
