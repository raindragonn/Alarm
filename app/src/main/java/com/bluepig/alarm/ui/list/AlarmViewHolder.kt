package com.bluepig.alarm.ui.list

import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemAlarmBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmViewHolder private constructor(
    private val _binding: ItemAlarmBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    private val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    private val meridiemFormat = SimpleDateFormat("a", Locale.getDefault())

    fun bind(alarm: Alarm) {
        _binding.apply {
            tvTime.text = timeFormat.format(alarm.timeInMillis)
            tvMeridiem.text = meridiemFormat.format(alarm.timeInMillis)
            tvTitle.text = alarm.media.title
            alarm.media
                .onMusic {

                }
                .onRingtone {

                }

            switchOnOff.isChecked = alarm.isActive
        }
    }

    companion object {
        fun create(
            binding: ItemAlarmBinding
        ): AlarmViewHolder {
            return AlarmViewHolder(binding)
        }
    }
}