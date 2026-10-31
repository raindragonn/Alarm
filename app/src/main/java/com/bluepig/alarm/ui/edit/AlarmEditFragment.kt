package com.bluepig.alarm.ui.edit

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.feature.edit.AlarmEditRoute
import com.bluepig.alarm.ui.alarm.AlarmActivity
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.AndroidEntryPoint

// 화면은 feature:edit. 인자 alarmId는 AlarmEditViewModel이 SavedStateHandle로 읽음 (기술 0021)
@AndroidEntryPoint
class AlarmEditFragment : Fragment() {
    private var selectedMedia by mutableStateOf<AlarmMedia?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(REQUEST_ALARM_MEDIA) { _, bundle ->
            selectedMedia = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(KEY_ALARM_MEDIA, AlarmMedia::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getSerializable(KEY_ALARM_MEDIA) as? AlarmMedia
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AlarmTheme {
                    AlarmEditRoute(
                        selectedMedia = selectedMedia,
                        onClose = { findNavController().popBackStack() },
                        onSelectMedia = ::openMediaSelect,
                        onPreview = {
                            AlarmActivity.openPreView(requireContext(), it)
                            BpLogger.logPreviewClick()
                        },
                        onSaved = { alarm, isNew ->
                            BpLogger.logAlarmSave(alarm, isNew, requireContext())
                            findNavController().navigate(
                                AlarmEditFragmentDirections.actionAlarmEditFragmentToAlarmListFragment(),
                            )
                        },
                        onDeleted = {
                            BpLogger.logAlarmDelete()
                            findNavController().popBackStack()
                        },
                    )
                }
            }
        }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(AlarmEditFragment::class.java.simpleName)
    }

    override fun onDestroy() {
        clearFragmentResultListener(REQUEST_ALARM_MEDIA)
        super.onDestroy()
    }

    private fun openMediaSelect() {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.alarmEditFragment) return
        navController.navigate(AlarmEditFragmentDirections.actionAlarmEditFragmentToMediaSelectFragment())
    }

    companion object {
        const val REQUEST_ALARM_MEDIA = "REQUEST_ALARM_MEDIA"
        const val KEY_ALARM_MEDIA = "KEY_ALARM_MEDIA"
    }
}
