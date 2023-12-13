package com.bluepig.alarm.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemSearchBinding
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.util.ext.setThumbnail

class SearchViewHolder private constructor(
    private val _binding: ItemSearchBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(file: MusicInfo) {
        _binding.apply {
            ivThumbnail.setThumbnail(file.thumbnail)
            tvTitle.text = file.title
        }
    }

    companion object {
        fun create(
            binding: ItemSearchBinding
        ): SearchViewHolder {
            return SearchViewHolder(binding)
        }
    }
}