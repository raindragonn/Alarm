package com.bluepig.alarm.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bluepig.alarm.databinding.ItemSearchBinding
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.util.ext.inflater

class SearchAdapter(
    private val _clickListener: (File) -> Unit
) : ListAdapter<File, SearchViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(
            parent.inflater,
            parent,
            false
        )
        return SearchViewHolder.create(binding).apply {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    _clickListener.invoke(currentList[bindingAdapterPosition])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<File>() {
            override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
                return oldItem.downloadPage == newItem.downloadPage
                        && oldItem.thumbnail == newItem.thumbnail
            }
        }
    }
}