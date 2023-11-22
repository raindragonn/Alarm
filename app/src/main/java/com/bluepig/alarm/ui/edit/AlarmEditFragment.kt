package com.bluepig.alarm.ui.edit

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmEditBinding
import com.bluepig.alarm.domain.entity.alarm.Weak
import com.bluepig.alarm.domain.result.onSuccess
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.viewLifeCycleScope
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmEditFragment : Fragment(R.layout.fragment_alarm_edit) {
    private val _binding: FragmentAlarmEditBinding by viewBinding(FragmentAlarmEditBinding::bind)
    private val _vm: AlarmEditViewModel by viewModels()

    @Inject
    lateinit var audioManager: AudioManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observing()
    }

    private fun initViews() = with(_binding) {
        timepicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            _vm.setTimeInMillis(hourOfDay, minute)
        }

        btnSunday.setOnClickListener { _vm.setRepeatWeak(Weak.SUNDAY) }
        btnMonday.setOnClickListener { _vm.setRepeatWeak(Weak.MONDAY) }
        btnTuesday.setOnClickListener { _vm.setRepeatWeak(Weak.TUESDAY) }
        btnWednesday.setOnClickListener { _vm.setRepeatWeak(Weak.WEDNESDAY) }
        btnThursday.setOnClickListener { _vm.setRepeatWeak(Weak.THURSDAY) }
        btnFriday.setOnClickListener { _vm.setRepeatWeak(Weak.FRIDAY) }
        btnSaturday.setOnClickListener { _vm.setRepeatWeak(Weak.SATURDAY) }

        ivThumbnail
            .setThumbnail(_vm.file.thumbnail)
        tvFileTitle.text = _vm.file.title

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekbarVolume.max = maxVolume
        seekbarVolume.progress = maxVolume
        seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                _vm.setVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            _vm.setVibration(isChecked)
        }

        etMemo.doAfterTextChanged {
            _vm.setMemo(it.toString())
        }

        btnSave.setOnClickListener {
            saveAlarm()
        }
    }

    private fun saveAlarm() = viewLifeCycleScope.launch {
        _vm.saveAlarm().onSuccess {
            val action =
                AlarmEditFragmentDirections.actionAlarmEditFragmentToAlarmListFragment()
            findNavController().navigate(action)
        }
    }

    private fun observing() {
        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            // TODO: 알람 수정 기능 추가 시 작성
        }
    }
}