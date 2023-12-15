package com.bluepig.alarm.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bluepig.alarm.databinding.ItemMediaBinding
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.util.ext.checkNoPosition
import com.bluepig.alarm.util.ext.inflater

class SearchAdapter(
    private val _clickListener: (MusicInfo) -> Unit
) : ListAdapter<MusicInfo, SearchViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemMediaBinding.inflate(
            parent.inflater,
            parent,
            false
        )
        return SearchViewHolder.create(binding).apply {
            binding.root.setOnClickListener {
                checkNoPosition {
                    _clickListener.invoke(currentList[bindingAdapterPosition])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<MusicInfo>() {
            override fun areItemsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
                return oldItem.downloadPage == newItem.downloadPage
                        && oldItem.thumbnail == newItem.thumbnail
            }
        }
    }
}