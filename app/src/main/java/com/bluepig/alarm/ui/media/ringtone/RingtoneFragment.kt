package com.bluepig.alarm.ui.media.ringtone

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentRingtoneBinding
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.ui.media.select.MediaSelectBottomSheetDialogFragment
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.logger.BpLogger
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RingtoneFragment : Fragment(R.layout.fragment_ringtone) {
    private val _binding: FragmentRingtoneBinding by viewBinding(FragmentRingtoneBinding::bind)
    private val _vm: RingtoneViewModel by viewModels()
    private val _adapter: RingtoneAdapter by lazy { RingtoneAdapter(::onClickRingtone) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observing()
    }

    override fun onResume() {
        super.onResume()
        BpLogger.logScreenView(RingtoneFragment::class.java.simpleName)
    }

    private fun initViews() = with(_binding) {
        rvRingtone.adapter = _adapter
    }

    private fun observing() {
        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            _vm.getRingtoneAlarm()
                .onSuccess(::bindRingtoneList)
        }
    }

    private fun bindRingtoneList(list: List<RingtoneMedia>) {
        _adapter.submitList(list)
    }

    private fun onClickRingtone(media: RingtoneMedia) {
        MediaSelectBottomSheetDialogFragment.openWithMedia(findNavController(), media)
    }
}