package com.bluepig.alarm.ui.media.music

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentMusicSearchBinding
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.isLoading
import com.bluepig.alarm.ui.media.select.MediaSelectBottomSheetDialogFragment
import com.bluepig.alarm.util.ext.setOnEnterListener
import com.bluepig.alarm.util.ext.setOnLoadMore
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn

@AndroidEntryPoint
class MusicSearchFragment : Fragment(R.layout.fragment_music_search) {
    private val _binding: FragmentMusicSearchBinding by viewBinding(FragmentMusicSearchBinding::bind)
    private val _vm: MusicSearchViewModel by viewModels()

    private val _adapter: MusicSearchAdapter by lazy { MusicSearchAdapter(::itemClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(MusicSearchFragment::class.java.simpleName)
    }

    private fun initViews() = with(_binding) {
        rvSearch.adapter = _adapter
        rvSearch.setOnLoadMore {
            _vm.search()
        }
        etSearch.setOnEnterListener {
            BpLogger.logMediaSearch(MusicMedia::class.java.simpleName, it)
            _vm.search(it)
        }

        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.musicInfoList
                .stateIn(this)
                .collect(::searchListHandle)
        }
    }

    private fun searchListHandle(result: Result<List<MusicInfo>>) {
        result.onSuccess { list ->
            setLoadingVisible(false)
            _adapter.submitList(list)
        }.onFailure {
            if (it.isLoading) {
                setLoadingVisible(true)
            } else {
                setLoadingVisible(false)
                showErrorToast(it)
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun itemClick(info: MusicInfo) {
        MediaSelectBottomSheetDialogFragment.openWithMusicInfo(findNavController(), info)
    }

    private fun setLoadingVisible(isVisible: Boolean) {
        _binding.pbLoading.isVisible = isVisible
    }
}