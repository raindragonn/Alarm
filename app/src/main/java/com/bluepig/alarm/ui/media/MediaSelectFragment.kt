package com.bluepig.alarm.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.designsystem.theme.AlarmTheme
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.feature.media.MediaSelectRoute
import com.bluepig.alarm.feature.media.MediaTab
import com.bluepig.alarm.ui.edit.AlarmEditFragment
import com.bluepig.alarm.ui.main.MainActivity
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.AndroidEntryPoint

// 화면은 feature:media. 고르면 편집 화면에 돌려줌 (기술 0021). 전면 광고는 저장 때만 (제품 0011).
// 유튜브 탭에서는 하단 광고를 숨김 (제품 0009)
@AndroidEntryPoint
class MediaSelectFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AlarmTheme {
                    MediaSelectRoute(
                        onBack = { findNavController().popBackStack() },
                        onSelected = ::select,
                        onTabChanged = { setBottomAdVisible(it != MediaTab.YouTube) },
                        onSearched = { tab, query ->
                            // 이전 로그와 같은 값 (MusicMedia·TubeMedia)
                            val type = if (tab == MediaTab.YouTube) TubeMedia::class else MusicMedia::class
                            BpLogger.logMediaSearch(type.java.simpleName, query)
                        },
                    )
                }
            }
        }

    override fun onDestroyView() {
        setBottomAdVisible(true)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(MediaSelectFragment::class.java.simpleName)
    }

    private fun select(media: AlarmMedia) {
        BpLogger.logMediaSelect(media)
        setFragmentResult(AlarmEditFragment.REQUEST_ALARM_MEDIA, bundleOf(AlarmEditFragment.KEY_ALARM_MEDIA to media))
        findNavController().popBackStack(R.id.alarmEditFragment, false)
    }

    private fun setBottomAdVisible(visible: Boolean) = (requireActivity() as MainActivity).setBottomAdVisible(visible)
}
