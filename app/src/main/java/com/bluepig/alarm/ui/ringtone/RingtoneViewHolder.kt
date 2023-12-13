package com.bluepig.alarm.ui.ringtone

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemSearchBinding
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia

class RingtoneViewHolder private constructor(
    private val _binding: ItemSearchBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(ringtone: RingtoneMedia) {
        _binding.apply {
            ivThumbnail.isVisible = false
            tvTitle.text = ringtone.title
        }
    }

    companion object {
        fun create(
            binding: ItemSearchBinding
        ): RingtoneViewHolder {
            return RingtoneViewHolder(binding)
        }
    }
}