package com.bluepig.alarm.ui.search.select

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentFileSelectBinding
import com.bluepig.alarm.domain.result.onSuccess
import com.bluepig.alarm.manager.player.SongPlayerManager
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@UnstableApi // TODO: Media3 향후 stable version으로 업데이트 필요
@AndroidEntryPoint
class FileSelectBottomSheetDialogFragment :
    BottomSheetDialogFragment(R.layout.fragment_file_select) {

    private val _binding: FragmentFileSelectBinding by viewBinding(FragmentFileSelectBinding::bind)
    private val _vm: FileSelectViewModel by viewModels()
    private val _navArgs: FileSelectBottomSheetDialogFragmentArgs by navArgs()

    @Inject
    lateinit var playerManager: SongPlayerManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerManager.init(viewLifecycleOwner.lifecycle, ::onPlayingStateChange)

        initViews()
        observing()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerManager.release()
    }

    private fun initViews() = with(_binding) {
        playerView.player = playerManager.getPlayer()
        val file = _navArgs.file
        tvTitle.text = file.title
        ivThumbnail.setThumbnail(file.thumbnail)
        btnClose.setOnClickListener { findNavController().popBackStack() }
        btnPlay.setOnClickListener { playerManager.playEndPause() }
    }

    /**
     * On playing state change
     * 현재 재생여부에 따른 콜백, 로딩이 끝난 다음에 불린다.
     * TODO: 플레이 여부 외에 현재 로딩 여부를 따로 정의하는것도 고려할만함
     * @param isPlaying 현재 플레이 여부를 나타낸다.
     */
    private fun onPlayingStateChange(isPlaying: Boolean) {
        with(_binding) {
            if (pbLoading.isVisible) {
                pbLoading.isVisible = false
                group.isVisible = true
            }

            btnPlay.text = if (isPlaying) "멈춤" else "재생"
        }
    }

    private fun observing() {
        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.fileUrlState
                .stateIn(this)
                .collect {
                    it.onSuccess(playerManager::setSongUrl)
                }
        }
    }
}