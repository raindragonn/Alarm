package com.bluepig.alarm.ui.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bluepig.alarm.R
import com.bluepig.alarm.databinding.FragmentAlarmEditBinding
import com.bluepig.alarm.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmEditFragment : Fragment(R.layout.fragment_alarm_edit) {
    private val _binding: FragmentAlarmEditBinding by viewBinding(FragmentAlarmEditBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {

    }
}