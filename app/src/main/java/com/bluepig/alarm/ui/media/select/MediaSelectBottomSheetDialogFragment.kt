package com.bluepig.alarm.ui.media.select

import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.DialogFragmentMediaSelectBinding
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.LoadingException
import com.bluepig.alarm.manager.player.MusicPlayerManager
import com.bluepig.alarm.ui.edit.AlarmEditFragment
import com.bluepig.alarm.util.ads.AdsManager
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MediaSelectBottomSheetDialogFragment :
    BottomSheetDialogFragment(R.layout.dialog_fragment_media_select) {

    private val _binding: DialogFragmentMediaSelectBinding by viewBinding(
        DialogFragmentMediaSelectBinding::bind
    )
    private val _vm: MediaSelectViewModel by viewModels()
    private val _adsManager by lazy { AdsManager(this) }

    @Inject
    lateinit var playerManager: MusicPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _adsManager.loadInterstitial()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerManager.init(viewLifecycleOwner.lifecycle,
            stateChangeListener = ::onPlayingStateChange,
            errorListener = { showErrorToast(it) })
        initViews()
        observing()
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(MediaSelectBottomSheetDialogFragment::class.java.simpleName)
    }

    override fun onDestroyView() {
        playerManager.release()
        super.onDestroyView()
    }

    @OptIn(UnstableApi::class)
    private fun initViews() = with(_binding) {
        playerView.player = playerManager.getPlayer()
        btnClose.setOnClickListener { findNavController().popBackStack() }
        btnPlay.setOnClickListener { playerManager.playEndPause() }
        btnSelect.setOnClickListener {
            playerManager.pause()
            _adsManager.showInterstitial(requireActivity(),
                onShowed = {
                    setLoadingState(true)
                }, onClose = {
                    openMedia()
                }, onLoadFail = {
                    openMedia()
                })
        }
    }

    private fun openMedia() {
        val alarmMedia = _vm.musicMedia.value.getOrNull()
        if (alarmMedia == null) {
            showErrorToast(null)
            return
        }
        BpLogger.logMediaSelect(alarmMedia)
        setFragmentResult(
            AlarmEditFragment.REQUEST_ALARM_MEDIA,
            bundleOf(AlarmEditFragment.KEY_ALARM_MEDIA to alarmMedia)
        )
        findNavController().popBackStack(R.id.alarmEditFragment, false)
    }

    private fun onPlayingStateChange(isPlaying: Boolean) {
        with(_binding) {
            btnPlay.text = if (isPlaying) getString(R.string.pause) else getString(R.string.play)
        }
    }

    private fun observing() {
        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.musicMedia.stateIn(this).collect { result ->
                result.onSuccess {
                    bindMedia(it)
                }.onFailure {
                    if (it is LoadingException) {
                        setLoadingState(true)
                    } else {
                        showErrorToast(it) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _binding.apply {
            pbLoading.isVisible = isLoading
            tvTitle.isVisible = isLoading.not()
            (playerView as View).isVisible = isLoading.not()
            ivThumbnail.isVisible = isLoading.not()
            btnPlay.isVisible = isLoading.not()
            btnSelect.isVisible = isLoading.not()
        }
    }

    private fun bindMedia(alarmMedia: AlarmMedia) {
        _binding.apply {
            setLoadingState(false)
            ivThumbnail.isVisible = alarmMedia.isMusic
            yp.isVisible = alarmMedia.isTube
            (playerView as View).isVisible = alarmMedia.isTube.not()

            tvTitle.text = alarmMedia.title

            alarmMedia.onMusic { music ->
                ivThumbnail.setThumbnail(music.thumbnail)
                playerManager.play(music)
            }.onRingtone { ringtone ->
                playerManager.play(ringtone)
            }.onTube {
                btnPlay.isVisible = false
                initYoutubePlayer(yp, it)
            }
        }
    }

    private fun initYoutubePlayer(yp: YouTubePlayerView, tubeMedia: TubeMedia) {
        viewLifecycleOwner.lifecycle.addObserver(yp)
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val controller = DefaultPlayerUiController(yp, youTubePlayer).apply {
                    showFullscreenButton(false)
                    showYouTubeButton(false)
                }
                yp.setCustomPlayerUi(controller.rootView)
                youTubePlayer.loadOrCueVideo(
                    viewLifecycleOwner.lifecycle, tubeMedia.videoId, 0f
                )
            }
        }
        val options = IFramePlayerOptions.Builder().controls(0).build()
        yp.initialize(listener, options)
    }

    companion object {
        const val KEY_ARGS_MUSIC_INFO = "musicInfo"
        const val KEY_ARGS_ALARM_MEDIA = "alarmMedia"

        private val _navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()

        fun openWithMedia(navController: NavController, media: AlarmMedia) {
            navController.navigate(
                R.id.MediaSelectBottomSheetDialogFragment, bundleOf(
                    KEY_ARGS_ALARM_MEDIA to media
                ), _navOptions
            )
        }

        fun openWithMusicInfo(navController: NavController, musicInfo: MusicInfo) {
            navController.navigate(
                R.id.MediaSelectBottomSheetDialogFragment, bundleOf(
                    KEY_ARGS_MUSIC_INFO to musicInfo
                ), _navOptions
            )
        }
    }
}