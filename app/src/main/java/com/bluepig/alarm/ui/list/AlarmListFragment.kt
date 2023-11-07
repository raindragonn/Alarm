package com.bluepig.alarm.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmListBinding
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmListFragment : Fragment(R.layout.fragment_alarm_list) {
    private val _binding: FragmentAlarmListBinding by viewBinding(FragmentAlarmListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        _binding.btn.setOnClickListener {
            val action = AlarmListFragmentDirections.actionAlarmListFragmentToSearchDialogFragment()
            findNavController().navigate(action)
        }
    }
}