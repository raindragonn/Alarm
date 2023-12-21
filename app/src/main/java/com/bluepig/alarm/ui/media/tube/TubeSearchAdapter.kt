package com.bluepig.alarm.ui.media.tube

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bluepig.alarm.databinding.ItemTubeBinding
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.util.ext.checkNoPosition
import com.bluepig.alarm.util.ext.inflater

class TubeSearchAdapter(
    private val _clickListener: (TubeMedia) -> Unit
) : ListAdapter<TubeMedia, TubeSearchViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TubeSearchViewHolder {
        val binding = ItemTubeBinding.inflate(
            parent.inflater,
            parent,
            false
        )
        return TubeSearchViewHolder.create(binding).apply {
            binding.root.setOnClickListener {
                checkNoPosition {
                    _clickListener.invoke(currentList[bindingAdapterPosition])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: TubeSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<TubeMedia>() {
            override fun areItemsTheSame(oldItem: TubeMedia, newItem: TubeMedia): Boolean {
                return oldItem.videoId == newItem.videoId
            }

            override fun areContentsTheSame(oldItem: TubeMedia, newItem: TubeMedia): Boolean {
                return oldItem.title == newItem.title
                        && oldItem.thumbnail == newItem.thumbnail
            }
        }
    }
}