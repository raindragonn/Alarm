package com.bluepig.alarm.ui.media.music

import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemMusicBinding
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.util.ext.setThumbnail

class MusicSearchViewHolder private constructor(
    private val _binding: ItemMusicBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(file: MusicInfo) {
        _binding.apply {
            ivThumbnail.setThumbnail(file.thumbnail)
            tvTitle.text = file.title
        }
    }

    companion object {
        fun create(
            binding: ItemMusicBinding
        ): MusicSearchViewHolder {
            return MusicSearchViewHolder(binding)
        }
    }
}