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
import com.bluepig.alarm.domain.result.NotFoundActiveAlarmException
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import com.bluepig.alarm.domain.result.onFailureWitLoading
import com.bluepig.alarm.manager.timeguide.TimeGuideManager
import com.bluepig.alarm.util.ext.viewLifeCycleScope
import com.bluepig.alarm.util.ext.viewRepeatOnLifeCycle
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    @Inject
    lateinit var timeGuideManager: TimeGuideManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observing()
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
                _vm.getAllAlarmsFlow()
                    .stateIn(this)
                    .collect(_alarmAdapter::submitList)
            }

            launch {
                _vm.getExpiredAlarmTime()
                    .stateIn(this)
                    .collect(::showTimeRemaining)
            }
        }
    }

    private fun showTimeRemaining(result: Result<Long>) {
        result
            .onSuccess { guideText ->
                _binding.tvAlarmState.text = timeGuideManager.getRemainingTimeGuide(guideText)
            }
            .onFailureWitLoading {
                when (it) {
                    NotFoundActiveAlarmException -> _binding.tvAlarmState.text =
                        getString(R.string.alarm_inactive_notice)

                    NotFoundAlarmException -> _binding.tvAlarmState.text =
                        getString(R.string.alarm_add_notice)
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