package com.bluepig.alarm.ui.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bluepig.alarm.databinding.ItemAlarmBinding
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.util.ext.checkNoPosition
import com.bluepig.alarm.util.ext.inflater

class AlarmAdapter(
    private val _itemClickListener: (Alarm) -> Unit,
    private val _switchClickListener: (Alarm) -> Unit
) : ListAdapter<Alarm, AlarmViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(
            parent.inflater,
            parent,
            false
        )
        return AlarmViewHolder.create(binding).apply {
            binding.root.setOnClickListener {
                checkNoPosition {
                    _itemClickListener.invoke(getItem(bindingAdapterPosition))
                }
            }

            binding.switchOnOff.setOnClickListener {
                checkNoPosition {
                    _switchClickListener.invoke(getItem(bindingAdapterPosition))
                }
            }
        }
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<Alarm>() {
            override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
                return oldItem.timeInMillis == newItem.timeInMillis
                        && oldItem.isActive == newItem.isActive
                        && oldItem.file == newItem.file
                        && oldItem.repeatWeak == newItem.repeatWeak
                        && oldItem.volume == newItem.volume
                        && oldItem.hasVibration == newItem.hasVibration
                        && oldItem.memo == newItem.memo
            }
        }
    }
}