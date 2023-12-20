package com.bluepig.alarm.ui.media.ringtone

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemMediaBinding
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia

class RingtoneViewHolder private constructor(
    private val _binding: ItemMediaBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(ringtone: RingtoneMedia) {
        _binding.apply {
            ivThumbnail.isVisible = false
            tvTitle.text = ringtone.title
        }
    }

    companion object {
        fun create(
            binding: ItemMediaBinding
        ): RingtoneViewHolder {
            return RingtoneViewHolder(binding)
        }
    }
}