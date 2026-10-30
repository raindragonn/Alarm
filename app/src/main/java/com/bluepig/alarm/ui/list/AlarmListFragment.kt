package com.bluepig.alarm.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.feature.list.AlarmListRoute
import com.bluepig.alarm.util.ads.AdsManager
import com.bluepig.alarm.util.ads.ListNativeAdRow
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// 화면은 feature:list. 마지막 PR에서 Navigation 3로 옮기며 없어짐 (기술 0021)
@AndroidEntryPoint
class AlarmListFragment : Fragment() {
    @Inject
    lateinit var adsManager: AdsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AlarmTheme {
                    AlarmListRoute(
                        onAddAlarm = { openEdit(null) },
                        onAlarmClick = ::openEdit,
                        onAlarmSaved = { BpLogger.logAlarmSave(it, false, requireContext()) },
                        onAlarmsLoaded = BpLogger::logAlarmTotalCount,
                        nativeAd = { ListNativeAdRow(adsManager, ::openStore, it) },
                    )
                }
            }
        }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(AlarmListFragment::class.java.simpleName)
    }

    private fun openEdit(alarm: Alarm?) {
        val navController = findNavController()
        // 연타로 두 번 이동하지 않게
        if (navController.currentDestination?.id != R.id.alarmListFragment) return
        navController.navigate(AlarmListFragmentDirections.actionAlarmListFragmentToAlarmEditFragment(alarm))
    }

    private fun openStore() {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://play.google.com/store/apps/details?id=${requireContext().packageName}".toUri()
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }.onFailure { showErrorToast(it) }
    }
}
