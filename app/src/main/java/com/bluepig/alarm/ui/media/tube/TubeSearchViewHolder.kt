package com.bluepig.alarm.ui.media.tube

import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemTubeBinding
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.util.ext.setThumbnail

class TubeSearchViewHolder private constructor(
    private val _binding: ItemTubeBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(tubeMedia: TubeMedia) {
        _binding.apply {
            ivThumbnail.setThumbnail(tubeMedia.thumbnail, false)
            tvTitle.text = tubeMedia.title
        }
    }

    companion object {
        fun create(
            binding: ItemTubeBinding
        ): TubeSearchViewHolder {
            return TubeSearchViewHolder(binding)
        }
    }
}