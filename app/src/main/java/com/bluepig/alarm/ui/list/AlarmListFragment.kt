package com.bluepig.alarm.ui.list

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmListBinding
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
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AlarmListFragment : Fragment(R.layout.fragment_alarm_list) {
    private val _binding: FragmentAlarmListBinding by viewBinding(FragmentAlarmListBinding::bind)
    private val _vm: AlarmListViewModel by viewModels()

    private val _alarmAdapter: AlarmAdapter by lazy { AlarmAdapter(::switchClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        _binding.rvAlarm.adapter = _alarmAdapter

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
                        Log.d("DEV_LOG", "$result")
                        result.onSuccess(_alarmAdapter::submitList)
                    }
            }
        }

        _binding.btnSearch.setOnClickListener {
            val action = AlarmListFragmentDirections.actionAlarmListFragmentToSearchFragment()
            findNavController().navigate(action)
        }
    }

    private fun showTimeRemaining(result: BpResult<Long>) {
        result
            .onSuccess { expireTime ->
                val dateFormat = SimpleDateFormat("약 mm분 남았습니다.", Locale.getDefault())
                _binding.tvAlarmState.text = dateFormat.format(expireTime)
            }
            .onFailure {
                when (it) {
                    NotFoundActiveAlarmException -> _binding.tvAlarmState.text = "모든 알람이 꺼진 상태입니다."
                    NotFoundAlarmException -> _binding.tvAlarmState.text = "알람을 추가해 주세요!"
                }
            }
    }


    private fun switchClick(position: Int) {
        val alarm = _alarmAdapter.currentList[position]
        viewLifeCycleScope.launch {
            _vm.saveAlarm(alarm)
                .onSuccess {
                    val onOff = if (it.isActive) "켰습니다" else "껏습니다."
                    Toast.makeText(requireContext(), "알람을 $onOff", Toast.LENGTH_SHORT).show()
                }
        }
    }
}