package com.bluepig.alarm.ui.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmEditBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.result.onSuccess
import com.bluepig.alarm.util.ext.viewLifeCycleScope
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class AlarmEditFragment : Fragment(R.layout.fragment_alarm_edit) {
    private val _binding: FragmentAlarmEditBinding by viewBinding(FragmentAlarmEditBinding::bind)
    private val _vm: AlarmEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        _binding.btnSave.setOnClickListener {
            val title = _binding.textInput.text?.toString() ?: "설정 안함"
            val currentTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, _binding.timepicker.hour)
                set(Calendar.MINUTE, _binding.timepicker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            viewLifeCycleScope.launch {
                val alarm = Alarm(
                    timeInMillis = currentTime,
                    isActive = true,
                    songName = title
                )

                _vm.saveAlarm(alarm).onSuccess {
                    val action =
                        AlarmEditFragmentDirections.actionAlarmEditFragmentToAlarmListFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }
}