package com.bluepig.alarm.ui.edit

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmEditBinding
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.file.SongFile
import com.bluepig.alarm.domain.result.NotSelectSongFile
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.domain.util.hourOfDay
import com.bluepig.alarm.domain.util.minute
import com.bluepig.alarm.ui.alarm.AlarmActivity
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.showErrorToast
import com.bluepig.alarm.util.ext.stringId
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

        btnSearch.setOnClickListener {
            goSearch()
        }

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
            kotlin.runCatching {
                _vm.getEditingAlarm() ?: throw NotSelectSongFile
            }.onSuccess { alarm ->
                AlarmActivity.openPreView(requireContext(), alarm)
            }.onFailure {
                showErrorToast(it)
            }
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
        }.onFailure {
            showErrorToast(it)
        }
    }

    private fun goSearch() {
        val action = AlarmEditFragmentDirections.actionAlarmEditFragmentToSearchFragment()
        findNavController().navigate(action)
    }

    private fun observing() = with(_vm) {
        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            launch {
                repeatWeek
                    .stateIn(this)
                    .collect(::bindWeek)
            }
            launch {
                songFile
                    .stateIn(this)
                    .collect(::bindSongFile)
            }
        }

        setFragmentResultListener(REQUEST_SONG_FILE) { _, bundle ->
            val songFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(KEY_SONG_FILE, SongFile::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getSerializable(KEY_SONG_FILE) as SongFile
            }
            setSongFile(songFile ?: return@setFragmentResultListener)
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

        val weekEntries = Week.entries
        val text = when {
            setWeek.isEmpty() ->
                getString(R.string.week_not_select_guide)

            setWeek == weekEntries.filter { it.isWeekend }.toSet() ->
                getString(R.string.week_weekend_guide)

            setWeek == weekEntries.filter { it.isWeekend.not() }.toSet() ->
                getString(R.string.week_weekday_guide)

            setWeek == weekEntries.toSet() ->
                getString(R.string.week_everyday_guide)

            else ->
                setWeek.sorted()
                    .joinToString(" ") { getString(it.stringId) } + getString(R.string.week_guide)
        }
        _binding.tvRepeatGuide.text = text
    }

    private fun bindSongFile(songFile: SongFile?) {
        _binding.groupMedia.isVisible = songFile != null
        songFile ?: return

        _binding.ivThumbnail.setThumbnail(songFile.thumbnail)
        _binding.tvFileTitle.text = songFile.title
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

    companion object {
        const val REQUEST_SONG_FILE = "REQUEST_SONG_FILE"
        const val KEY_SONG_FILE = "KEY_SONG_FILE"
    }
}