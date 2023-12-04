package com.bluepig.alarm.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmListBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.result.NotFoundActiveAlarmException
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import com.bluepig.alarm.domain.result.onFailure
import com.bluepig.alarm.domain.result.onSuccess
import com.bluepig.alarm.util.ext.viewLifeCycleScope
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmListFragment : Fragment(R.layout.fragment_alarm_list) {
    private val _binding: FragmentAlarmListBinding by viewBinding(FragmentAlarmListBinding::bind)
    private val _vm: AlarmListViewModel by viewModels()

    private val _alarmAdapter: AlarmAdapter by lazy {
        AlarmAdapter(
            ::onItemClick,
            ::onItemSwitchClick
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observing()
        _vm.refresh()
    }

    private fun initViews() {
        _binding.rvAlarm.adapter = _alarmAdapter

        _binding.btnSearch.setOnClickListener {
            val action =
                AlarmListFragmentDirections.actionAlarmListFragmentToAlarmEditFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun observing() {
        viewRepeatOnLifeCycle(Lifecycle.State.STARTED) {
            launch {
                _vm.expireTime
                    .stateIn(this)
                    .collect(::showTimeRemaining)
            }

            launch {
                _vm.alarmList
                    .stateIn(this)
                    .collect { result ->
                        result.onSuccess(_alarmAdapter::submitList)
                    }
            }
        }
    }

    private fun showTimeRemaining(result: BpResult<String>) {
        result
            .onSuccess { guideText ->
                _binding.tvAlarmState.text = guideText
            }
            .onFailure {
                when (it) {
                    NotFoundActiveAlarmException -> _binding.tvAlarmState.text = "모든 알람이 꺼진 상태입니다."
                    NotFoundAlarmException -> _binding.tvAlarmState.text = "알람을 추가해 주세요!"
                }
            }
    }

    private fun onItemClick(alarm: Alarm) {
        val action =
            AlarmListFragmentDirections.actionAlarmListFragmentToAlarmEditFragment(alarm)
        findNavController().navigate(action)
    }

    private fun onItemSwitchClick(alarm: Alarm) {
        viewLifeCycleScope.launch {
            _vm.alarmActiveSwitching(alarm)
        }
    }
}