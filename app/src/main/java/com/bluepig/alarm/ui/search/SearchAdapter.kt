package com.bluepig.alarm.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bluepig.alarm.databinding.ItemSearchBinding
import com.bluepig.alarm.domain.entity.file.BasicFile
import com.bluepig.alarm.util.ext.checkNoPosition
import com.bluepig.alarm.util.ext.inflater

class SearchAdapter(
    private val _clickListener: (BasicFile) -> Unit
) : ListAdapter<BasicFile, SearchViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(
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
        private val differ = object : DiffUtil.ItemCallback<BasicFile>() {
            override fun areItemsTheSame(oldItem: BasicFile, newItem: BasicFile): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BasicFile, newItem: BasicFile): Boolean {
                return oldItem.downloadPage == newItem.downloadPage
                        && oldItem.thumbnail == newItem.thumbnail
            }
        }
    }
}