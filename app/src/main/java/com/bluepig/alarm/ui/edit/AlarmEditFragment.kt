package com.bluepig.alarm.ui.edit

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmEditBinding
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.result.onSuccess
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.domain.util.hourOfDay
import com.bluepig.alarm.domain.util.minute
import com.bluepig.alarm.ui.alarm.AlarmActivity
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.viewLifeCycleScope
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
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
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        bindTimePicker(_vm.timeInMillis.value)
        timepicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            _vm.setTimeInMillis(hourOfDay, minute)
        }

        btnSunday.setOnclickWeek(_vm::setRepeatWeek)
        btnMonday.setOnclickWeek(_vm::setRepeatWeek)
        btnTuesday.setOnclickWeek(_vm::setRepeatWeek)
        btnWednesday.setOnclickWeek(_vm::setRepeatWeek)
        btnThursday.setOnclickWeek(_vm::setRepeatWeek)
        btnFriday.setOnclickWeek(_vm::setRepeatWeek)
        btnSaturday.setOnclickWeek(_vm::setRepeatWeek)

        ivThumbnail
            .setThumbnail(_vm.songFile.thumbnail)
        tvFileTitle.text = _vm.songFile.title

        bindVolume(_vm.volume.value)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekbarVolume.max = maxVolume
        seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                _vm.setVolume(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        bindVibration(_vm.vibration.value)
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            _vm.setVibration(isChecked)
        }

        bindMemo(_vm.memo.value)
        etMemo.doAfterTextChanged {
            _vm.setMemo(it.toString())
        }

        btnRemove.isVisible = _vm.isEdit
        btnRemove.setOnClickListener {
            removeAlarm()
        }

        btnSave.setOnClickListener {
            saveAlarm()
        }

        btnPreview.setOnClickListener {
            AlarmActivity.openPreView(requireContext(), _vm.getEditingAlarm())
        }
    }

    private fun removeAlarm() = viewLifeCycleScope.launch {
        _vm.removeAlarm().onSuccess {
            findNavController().popBackStack()
        }
    }

    private fun saveAlarm() = viewLifeCycleScope.launch {
        _vm.saveAlarm().onSuccess {
            val action =
                AlarmEditFragmentDirections.actionAlarmEditFragmentToAlarmListFragment()
            findNavController().navigate(action)
        }
    }

    private fun observing() = with(_vm) {
        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            repeatWeek
                .stateIn(this)
                .collect(::bindWeek)
        }
    }

    private fun bindTimePicker(timeInMillis: Long) {
        _binding.timepicker.hour = CalendarHelper.fromTimeInMillis(timeInMillis).hourOfDay
        _binding.timepicker.minute = CalendarHelper.fromTimeInMillis(timeInMillis).minute
    }

    private fun bindWeek(setWeek: Set<Week>) {
        val buttons = listOf(
            _binding.btnSunday,
            _binding.btnMonday,
            _binding.btnTuesday,
            _binding.btnWednesday,
            _binding.btnThursday,
            _binding.btnFriday,
            _binding.btnSaturday,
        )

        buttons.forEach {
            if (setWeek.contains(it.getWeek())) {
                it.setSelected()
            } else {
                it.unSelected()
            }
        }
    }

    private fun bindVolume(volume: Int) {
        _binding.seekbarVolume.progress = volume
    }

    private fun bindVibration(switch: Boolean) {
        _binding.switchVibration.isChecked = switch
        _binding.switchVibration.jumpDrawablesToCurrentState()
    }

    private fun bindMemo(memo: String) {
        _binding.etMemo.setText(memo)
    }
}