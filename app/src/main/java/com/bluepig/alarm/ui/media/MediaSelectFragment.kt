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
import com.bluepig.alarm.util.ads.AdsManager
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// 화면은 feature:media. 고르면 전면 광고(1시간 1번)를 거쳐 편집 화면에 돌려줌 (기술 0021)
@AndroidEntryPoint
class MediaSelectFragment : Fragment() {
    @Inject
    lateinit var adsManager: AdsManager

    private var isSelecting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsManager.loadInterstitial(lifecycle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AlarmTheme {
                    MediaSelectRoute(
                        onBack = { findNavController().popBackStack() },
                        onSelected = ::select,
                        onSearched = { tab, query ->
                            // 이전 로그와 같은 값 (MusicMedia·TubeMedia)
                            val type = if (tab == MediaTab.YouTube) TubeMedia::class else MusicMedia::class
                            BpLogger.logMediaSearch(type.java.simpleName, query)
                        },
                    )
                }
            }
        }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(MediaSelectFragment::class.java.simpleName)
    }

    private fun select(media: AlarmMedia) {
        if (isSelecting) return
        isSelecting = true
        adsManager.showInterstitial(
            activity = requireActivity(),
            onShowed = {},
            onCloseOrFailed = {
                BpLogger.logMediaSelect(media)
                setFragmentResult(
                    AlarmEditFragment.REQUEST_ALARM_MEDIA,
                    bundleOf(AlarmEditFragment.KEY_ALARM_MEDIA to media),
                )
                findNavController().popBackStack(R.id.alarmEditFragment, false)
            },
        )
    }
}
