package com.bluepig.alarm.ui.media.ringtone

import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemRingtoneBinding
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia

class RingtoneViewHolder private constructor(
    private val _binding: ItemRingtoneBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(ringtone: RingtoneMedia) {
        _binding.apply {
            tvTitle.text = ringtone.title
        }
    }

    companion object {
        fun create(
            binding: ItemRingtoneBinding
        ): RingtoneViewHolder {
            return RingtoneViewHolder(binding)
        }
    }
}