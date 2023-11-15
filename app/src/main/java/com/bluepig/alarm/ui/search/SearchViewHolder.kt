package com.bluepig.alarm.ui.search

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bluepig.alarm.databinding.ItemSearchBinding
import com.bluepig.alarm.domain.entity.file.File

class SearchViewHolder private constructor(
    private val _binding: ItemSearchBinding,
) : RecyclerView.ViewHolder(_binding.root) {

    fun bind(file: File) {
        _binding.apply {
            ivThumbnail.load(file.thumbnail) {
                crossfade(true)
            }
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