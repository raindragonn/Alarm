package com.bluepig.alarm.ui.edit

import android.annotation.SuppressLint
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.text.ParcelableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
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
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.result.NotSelectAlarmMedia
import com.bluepig.alarm.domain.result.onFailureWitLoading
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.domain.util.hourOfDay
import com.bluepig.alarm.domain.util.minute
import com.bluepig.alarm.ui.alarm.AlarmActivity
import com.bluepig.alarm.util.ext.createSpan
import com.bluepig.alarm.util.ext.getGuideText
import com.bluepig.alarm.util.ext.setDefaultColor
import com.bluepig.alarm.util.ext.setThumbnail
import com.bluepig.alarm.util.ext.showErrorToast
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
        tvTitle.text =
            if (_vm.isEdit) getString(R.string.alarm_edit) else getString(R.string.alarm_create)

        initTimePicker(_vm.timeInMillis.value)
        initWeekButtons(_vm::setRepeatWeek)
        initVolume(_vm.volume.value)
        initVolumeAutoIncrease(_vm.volumeAutoIncrease.value)
        initVibration(_vm.vibration.value)
        initMemo(_vm.memo.value)
        initDeleteButton()

        btnBack.setOnClickListener { findNavController().popBackStack() }
        btnMediaSelect.setOnClickListener { openMediaSelect() }
        btnSave.setOnClickListener { saveAlarm() }
        btnPreview.setOnClickListener { openPreview() }
    }

    private fun initWeekButtons(onClick: (Week) -> Unit) {
        _binding.apply {
            btnSunday.setOnclickWeek(onClick)
            btnMonday.setOnclickWeek(onClick)
            btnTuesday.setOnclickWeek(onClick)
            btnWednesday.setOnclickWeek(onClick)
            btnThursday.setOnclickWeek(onClick)
            btnFriday.setOnclickWeek(onClick)
            btnSaturday.setOnclickWeek(onClick)
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
        }.onFailureWitLoading {
            showErrorToast(it)
        }
    }

    private fun openMediaSelect() {
        val action = AlarmEditFragmentDirections.actionAlarmEditFragmentToMediaSelectFragment()
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
                alarmMedia
                    .stateIn(this)
                    .collect(::bindAlarmMedia)
            }
            launch {
                getExpiredTime()
                    .stateIn(this)
                    .collect(_binding.tvTimeReminder::setText)
            }
        }

        setFragmentResultListener(REQUEST_ALARM_MEDIA) { _, bundle ->
            val alarmMedia = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(KEY_ALARM_MEDIA, AlarmMedia::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getSerializable(KEY_ALARM_MEDIA) as AlarmMedia
            }
            setAlarmMedia(alarmMedia ?: return@setFragmentResultListener)
        }
    }

    private fun openPreview() {
        kotlin.runCatching {
            _vm.getEditingAlarm() ?: throw NotSelectAlarmMedia
        }.onSuccess { alarm ->
            AlarmActivity.openPreView(requireContext(), alarm)
        }.onFailureWitLoading {
            showErrorToast(it)
        }
    }

    private fun initTimePicker(timeInMillis: Long) {
        _binding.apply {
            timepicker.hour = CalendarHelper.fromTimeInMillis(timeInMillis).hourOfDay
            timepicker.minute = CalendarHelper.fromTimeInMillis(timeInMillis).minute
            timepicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                _vm.setTimeInMillis(hourOfDay, minute)
            }
        }
    }

    private fun bindWeek(setWeek: Set<Week>) {
        _binding.apply {
            val buttons = listOf(
                btnSunday,
                btnMonday,
                btnTuesday,
                btnWednesday,
                btnThursday,
                btnFriday,
                btnSaturday,
            )

            buttons.forEach {
                if (setWeek.contains(it.getWeek())) {
                    it.setSelected()
                } else {
                    it.unSelected()
                }
            }
            tvRepeat.text = setWeek.getGuideText(requireContext())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindAlarmMedia(alarmMedia: AlarmMedia?) {
        val highlightSpans = arrayOf<ParcelableSpan>(
            TextAppearanceSpan(
                requireContext(), R.style.TextAppearance_Alarm_Body2
            ), ForegroundColorSpan(requireContext().getColor(R.color.primary_600))
        )
        _binding.apply {
            if (alarmMedia == null) {
                ivThumbnail.isVisible = false
                tvMediaTitle.text = getString(R.string.alarm_media_required_notice)
            } else {
                alarmMedia.onMusic {
                    ivThumbnail.setThumbnail(it.thumbnail)
                    tvMediaTitle.text = it.title
                    val titleText = getString(R.string.title)
                    tvMediaTitle.text = "$titleText  ${it.title}".createSpan(
                        0, titleText.length, *highlightSpans
                    )

                }.onRingtone {
                    ivThumbnail.isVisible = false
                    val ringtoneGuideText = getString(R.string.ringtone)
                    tvMediaTitle.text = "$ringtoneGuideText  ${it.title}".createSpan(
                        0, ringtoneGuideText.length, *highlightSpans
                    )
                }
            }

        }
    }

    private fun initVolume(volume: Int) {
        _binding.apply {
            seekbarVolume.progress = volume
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            seekbarVolume.max = maxVolume
            seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    _vm.setVolume(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun initVolumeAutoIncrease(switch: Boolean) {
        _binding.apply {
            switchVolumeAutoIncrease.setDefaultColor()
            switchVolumeAutoIncrease.isChecked = switch
            switchVolumeAutoIncrease.setDefaultColor()
            switchVolumeAutoIncrease.setOnCheckedChangeListener { _, isChecked ->
                _vm.setVolumeAutoIncrease(isChecked)
            }
        }
    }

    private fun initVibration(switch: Boolean) {
        _binding.apply {
            switchVibration.setDefaultColor()
            switchVibration.isChecked = switch
            switchVibration.jumpDrawablesToCurrentState()
            switchVibration.setOnCheckedChangeListener { _, isChecked ->
                _vm.setVibration(isChecked)
            }
        }
    }

    private fun initMemo(memo: String) {
        _binding.apply {
            etMemo.setText(memo)
            etMemo.doAfterTextChanged { _vm.setMemo(it.toString()) }
            switchTts.setDefaultColor()
            switchTts.setOnClickListener {
                // TODO: TTS 기능 추가 예정
                Toast.makeText(requireContext(), "해당 기능은 아직 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initDeleteButton() {
        _binding.apply {
            btnDelete.isVisible = _vm.isEdit
            btnDelete.setOnClickListener {
                removeAlarm()
            }
        }
    }

    companion object {
        const val REQUEST_ALARM_MEDIA = "REQUEST_ALARM_MEDIA"
        const val KEY_ALARM_MEDIA = "KEY_ALARM_MEDIA"
    }
}